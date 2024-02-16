package com.example.gmall.service.item.service.impl;

import com.example.gmall.service.item.feign.SkuDetailFeignClient;
import com.example.gmall.service.item.service.SkuDetailService;
import com.example.gmall.service.product.entity.SkuImage;
import com.example.gmall.service.product.entity.SkuInfo;
import com.example.gmall.service.product.entity.SpuSaleAttr;
import com.example.gmall.service.product.vo.CategoryTreeVO;
import com.example.gmall.service.product.vo.SkuDetailVO;
import com.example.gmall.service.product.vo.SkuDetailVO.CategoryViewDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 15/2/2024 - 4:07 pm
 * @Description
 */
@Service
public class SkuDetailServiceImpl implements SkuDetailService {

    @Autowired
    SkuDetailFeignClient skuDetailFeignClient;

    @Override
    public SkuDetailVO getSkuDetailData(Long skuId) {
        SkuDetailVO skuDetailVO = new SkuDetailVO();

        //我们这里需要的参数是三级分类的id，但是方法参数目前只有skuId，所以我们需要远程调用skuDetailFeignClient来获取三级分类的id
        //skuDetailFeignClient.getCategoryTreeWithC3Id();
        SkuInfo skuInfo = skuDetailFeignClient.getSkuInfo(skuId).getData();
        List<SkuImage> skuImageList = skuDetailFeignClient.getSkuImages(skuId).getData();
        skuInfo.setSkuImageList(skuImageList);
        skuDetailVO.setSkuInfo(skuInfo);

        CategoryTreeVO categoryTreeVO = skuDetailFeignClient.getCategoryTreeWithC3Id(skuInfo.getCategory3Id()).getData();
        //得到CategoryTreeVO，需要CategoryViewDTO，所以类型要转换一下
        //BeanUtils.copyProperties(categoryTreeVO, categoryViewDTO); //用不了，因为CategoryTreeVO是多层嵌套的
        CategoryViewDTO categoryViewDTO = convertToCategoryViewDTO(categoryTreeVO);
        skuDetailVO.setCategoryView(categoryViewDTO);

        BigDecimal price = skuDetailFeignClient.getPrice(skuInfo.getSpuId()).getData();
        skuDetailVO.setPrice(price);

        List<SpuSaleAttr> spuSaleAttrs = skuDetailFeignClient.getSpuSaleAttr(skuInfo.getSpuId(), skuId).getData();
        skuDetailVO.setSpuSaleAttrList(spuSaleAttrs);

        String jsonString = skuDetailFeignClient.getValuesSkuJson(skuInfo.getSpuId()).getData();
        skuDetailVO.setValuesSkuJson(jsonString);

        return skuDetailVO;
    }

    private CategoryViewDTO convertToCategoryViewDTO(CategoryTreeVO categoryTreeVO) {
        CategoryViewDTO categoryViewDTO = new CategoryViewDTO();
        categoryViewDTO.setCategory1Id(categoryTreeVO.getCategoryId());
        categoryViewDTO.setCategory1Name(categoryTreeVO.getCategoryName());

        CategoryTreeVO child1 = categoryTreeVO.getCategoryChild().get(0);
        categoryViewDTO.setCategory2Id(child1.getCategoryId());
        categoryViewDTO.setCategory2Name(child1.getCategoryName());

        CategoryTreeVO child2 = child1.getCategoryChild().get(0);
        categoryViewDTO.setCategory3Id(child2.getCategoryId());
        categoryViewDTO.setCategory3Name(child2.getCategoryName());

        return categoryViewDTO;
    }
}
