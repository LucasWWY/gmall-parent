package com.example.gmall.service.product.vo;

import com.example.gmall.service.product.entity.SkuInfo;
import com.example.gmall.service.product.entity.SpuSaleAttr;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 15/2/2024 - 3:46 pm
 * @Description
 */
@Data
public class SkuDetailVO {

    //1、分类信息
    private CategoryViewDTO categoryView;

    //2、sku信息
    private SkuInfo skuInfo;

    //3、实时价格
    private BigDecimal price;

    //4、销售属性集合
    private List<SpuSaleAttr> spuSaleAttrList;

    //5、valuesSkuJson
    private String valuesSkuJson;



    @Data
    public static class CategoryViewDTO {
        private Long category1Id;
        private Long category2Id;
        private Long category3Id;
        private String category1Name;
        private String category2Name;
        private String category3Name;
    }


}
