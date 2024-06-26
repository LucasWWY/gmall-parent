package com.example.gmall.model.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * spu销售属性
 * @TableName spu_sale_attr
 */
@TableName(value ="spu_sale_attr")
@Data
public class SpuSaleAttr implements Serializable {
    /**
     * 编号(业务中无关联)
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品id
     */
    private Long spuId;

    /**
     * 销售属性id
     */
    private Long baseSaleAttrId;

    /**
     * 销售属性名称(冗余)
     */
    private String saleAttrName;

    //返回给前端需要，如果是前端传来的数据，需要用VO类来接收
    @TableField(exist = false)
    private List<SpuSaleAttrValue> spuSaleAttrValueList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}