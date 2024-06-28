package com.example.gmall.model.product.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 14/2/2024 - 1:48 am
 * @Description
 */
@NoArgsConstructor
@Data
public class SkuSaveInfoVO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("spuId")
    private Long spuId;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("skuName")
    private String skuName;

    @JsonProperty("skuDesc")
    private String skuDesc;

    @JsonProperty("weight")
    private BigDecimal weight;

    @JsonProperty("tmId")
    private Long tmId;

    @JsonProperty("category3Id")
    private Long category3Id;

    @JsonProperty("skuDefaultImg")
    private String skuDefaultImg;

    @JsonProperty("skuImageList")
    private List<SkuImageListDTO> skuImageList;

    @JsonProperty("skuAttrValueList")
    private List<SkuAttrValueListDTO> skuAttrValueList;

    @JsonProperty("skuSaleAttrValueList")
    private List<SkuSaleAttrValueListDTO> skuSaleAttrValueList;

    @NoArgsConstructor
    @Data
    public static class SkuImageListDTO {
        @JsonProperty("spuImgId")
        private Long spuImgId;

        @JsonProperty("imgName")
        private String imgName;

        @JsonProperty("imgUrl")
        private String imgUrl;

        @JsonProperty("isDefault")
        private String isDefault;
    }

    @NoArgsConstructor
    @Data
    public static class SkuAttrValueListDTO {
        @JsonProperty("attrId")
        private Long attrId;

        @JsonProperty("valueId")
        private Long valueId;
    }

    @NoArgsConstructor
    @Data
    public static class SkuSaleAttrValueListDTO {
        @JsonProperty("saleAttrValueId")
        private Long saleAttrValueId;

        @JsonProperty("saleAttrValueName")
        private String saleAttrValueName;

        @JsonProperty("baseSaleAttrId")
        private Long baseSaleAttrId;

        @JsonProperty("saleAttrName")
        private String saleAttrName;
    }

}
