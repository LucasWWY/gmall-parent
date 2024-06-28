package com.example.gmall.model.product.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 13/2/2024 - 6:18 am
 * @Description 前段发送来的JSON数据是复合的，对应好几个数据库，所以需要一个VO类来接收，然后再分别保存到对应的数据库中
 */
@NoArgsConstructor
@Data
public class SpuSaveInfoVO {
    //以下内容由GsonFormat插件自动生成

    @JsonProperty("id")
    private Long id;

    @JsonProperty("spuName")
    private String spuName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("category3Id")
    private Long category3Id;

    @JsonProperty("tmId")
    private Long tmId;
    //以上信息保存到 spu_info 表中

    //以下内容由GsonFormat插件自动生成，正常应该叫SpuImageVO
    @JsonProperty("spuImageList")
    private List<SpuImageListDTO> spuImageList;

    @JsonProperty("spuSaleAttrList")
    private List<SpuSaleAttrListDTO> spuSaleAttrList;

    @NoArgsConstructor
    @Data
    public static class SpuImageListDTO {
        @JsonProperty("imgName")
        private String imgName;

        @JsonProperty("imgUrl")
        private String imgUrl;
    }
    //对应的数据库是spu_image

    @NoArgsConstructor
    @Data
    public static class SpuSaleAttrListDTO {
        @JsonProperty("baseSaleAttrId")
        private Long baseSaleAttrId;

        @JsonProperty("saleAttrName")
        private String saleAttrName;

        @JsonProperty("spuSaleAttrValueList")
        private List<SpuSaleAttrValueListDTO> spuSaleAttrValueList;

        @NoArgsConstructor
        @Data
        public static class SpuSaleAttrValueListDTO {
            @JsonProperty("baseSaleAttrId")
            private Long baseSaleAttrId;

            @JsonProperty("saleAttrValueName")
            private String saleAttrValueName;
        }
    }
    //对应的数据库是spu_sale_attr 和 spu_sale_attr_value
}
