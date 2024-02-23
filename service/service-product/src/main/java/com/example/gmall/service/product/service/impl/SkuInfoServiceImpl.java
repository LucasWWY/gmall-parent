package com.example.gmall.service.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.gmall.common.constant.RedisConst;
import com.example.gmall.service.product.entity.SkuAttrValue;
import com.example.gmall.service.product.entity.SkuImage;
import com.example.gmall.service.product.entity.SkuInfo;
import com.example.gmall.service.product.entity.SkuSaleAttrValue;
import com.example.gmall.service.product.mapper.SkuInfoMapper;
import com.example.gmall.service.product.service.SkuAttrValueService;
import com.example.gmall.service.product.service.SkuImageService;
import com.example.gmall.service.product.service.SkuInfoService;
import com.example.gmall.service.product.service.SkuSaleAttrValueService;
import com.example.gmall.service.product.vo.SkuSaveInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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
}




