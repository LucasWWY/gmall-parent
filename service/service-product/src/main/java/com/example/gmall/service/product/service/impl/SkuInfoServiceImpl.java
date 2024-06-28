package com.example.gmall.service.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.gmall.common.constant.RedisConst;
import com.example.gmall.feign.search.SearchFeignClient;
import com.example.gmall.model.product.entity.*;
import com.example.gmall.model.search.Goods;
import com.example.gmall.model.search.SearchAttr;
import com.example.gmall.service.product.mapper.SkuInfoMapper;
import com.example.gmall.service.product.service.*;
import com.example.gmall.model.product.vo.CategoryTreeVO;
import com.example.gmall.model.product.vo.SkuSaveInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author wangweiyedemacbook
* @description 针对表【sku_info(库存单元表)】的数据库操作Service实现
* @createDate 2024-02-06 23:58:53
*/
@Slf4j
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo>
    implements SkuInfoService{

    @Autowired
    SkuImageService skuImageService;

    @Autowired
    SkuAttrValueService skuAttrValueService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired //远程调用ES
    SearchFeignClient searchFeignClient;

    @Autowired
    BaseTrademarkService baseTrademarkService;

    @Autowired
    BaseCategory1Service baseCategory1Service;

    @Override
    public void saveSkuInfoData(SkuSaveInfoVO skuSaveInfoVO) {
        //log.info("skuSaveInfoVO: {}", skuSaveInfoVO);
        //1. 存sku_info
        SkuInfo skuInfo = new SkuInfo();
        BeanUtils.copyProperties(skuSaveInfoVO, skuInfo);
        save(skuInfo);

        Long skuId = skuInfo.getId();
        //2. 存sku_image
        List<SkuImage> skuImages = skuSaveInfoVO.getSkuImageList()
                .stream()
                .map(item -> {
                    SkuImage skuImage = new SkuImage();
                    BeanUtils.copyProperties(item, skuImage);
                    skuImage.setSkuId(skuId);
                    return skuImage;
                }).collect(Collectors.toList());
        skuImageService.saveBatch(skuImages);

        //3. 存sku_attr_value
        List<SkuAttrValue> skuAttrValues = skuSaveInfoVO.getSkuAttrValueList()
                .stream()
                .map(item -> {
                    SkuAttrValue skuAttrValue = new SkuAttrValue();
                    BeanUtils.copyProperties(item, skuAttrValue);
                    skuAttrValue.setSkuId(skuId);
                    return skuAttrValue;
                }).collect(Collectors.toList());
        skuAttrValueService.saveBatch(skuAttrValues);

        //4. 存sku_sale_attr_value
        List<SkuSaleAttrValue> skuSaleAttrValues = skuSaveInfoVO.getSkuSaleAttrValueList()
                .stream()
                .map(item -> {
                    SkuSaleAttrValue skuSaleAttrValue = new SkuSaleAttrValue();
                    //BeanUtils.copyProperties(item, skuSaleAttrValue); //前端就一个可以用到
                    skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
                    skuSaleAttrValue.setSaleAttrValueId(item.getSaleAttrValueId());
                    skuSaleAttrValue.setSkuId(skuId);
                    return skuSaleAttrValue;
                }).collect(Collectors.toList());
        skuSaleAttrValueService.saveBatch(skuSaleAttrValues);

        //每录入一个商品，都要同步bitmap
        redisTemplate.opsForValue().setBit(RedisConst.SKUID_BITMAP, skuId, true);
    }

    @Override
    public void upGoods(Long skuId) {
        //1、修改数据库的上架状态
        boolean update = lambdaUpdate() //mybatis-plus的lambdaUpdate
                .set(SkuInfo::getIsSale, 1) //修改SkuInfo会自动找到sku_info表中的一行数据
                .eq(SkuInfo::getId, skuId).update();

        //2、保存到es中
        if(update){
            //通过skuId查询到ES需要的信息Goods
            Goods goods = prepareGoods(skuId);

            //通过远程调用，将商品信息保存到ES中
            searchFeignClient.up(goods); //skuInfoServiceImpl(goods) 索引通过@Document(indexName = "goods" , shards = 3, replicas = 2)已自动创建 -> searchFeignClient (common) -> searchRpcController (service-search) -> searchServiceImpl -> goodsRepository.save(goods);
            log.info("商品【{}】 上架完成",skuId);
        }
    }

    //将上架了的商品信息存到ES中
    private Goods prepareGoods(Long skuId) {
        SkuInfo info = getById(skuId);
        Goods goods = new Goods();
        goods.setId(info.getId());
        goods.setDefaultImg(info.getSkuDefaultImg());
        goods.setTitle(info.getSkuName());
        goods.setPrice(info.getPrice().doubleValue());
        goods.setCreateTime(new Date());

        //查询品牌
        BaseTrademark trademark = baseTrademarkService.getById(info.getTmId());
        goods.setTmId(info.getTmId());
        goods.setTmName(trademark.getTmName());
        goods.setTmLogoUrl(trademark.getLogoUrl());


        //三级分类信息
        CategoryTreeVO tree = baseCategory1Service.getCategoryTreeWithC3Id(info.getCategory3Id());
        goods.setCategory1Id(tree.getCategoryId());
        goods.setCategory1Name(tree.getCategoryName());

        CategoryTreeVO child = tree.getCategoryChild().get(0);
        goods.setCategory2Id(child.getCategoryId());
        goods.setCategory2Name(child.getCategoryName());

        CategoryTreeVO child2 = child.getCategoryChild().get(0);
        goods.setCategory3Id(child2.getCategoryId());
        goods.setCategory3Name(child2.getCategoryName());

        //热度分
        goods.setHotScore(0L);

        //商品的所有平台属性的名和值
        List<SearchAttr> attrs = skuAttrValueService.getSkuAttrsAndValue(skuId);
        goods.setAttrs(attrs);
        return goods;
    }

    @Override
    public void downGoods(Long skuId) {
        //1、修改数据库的上架状态
        boolean update = lambdaUpdate()
                .set(SkuInfo::getIsSale, 0)
                .eq(SkuInfo::getId, skuId).update();

        //2、远程下架
        if(update){
            searchFeignClient.down(skuId);
            //TODO: 上下架的细节：缓存、bitmap都要同步删除
            log.info("商品【{}】 下架完成",skuId);
        }
    }
}




