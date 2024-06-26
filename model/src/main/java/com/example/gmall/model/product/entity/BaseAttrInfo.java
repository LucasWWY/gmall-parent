package com.example.gmall.model.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 属性表
 * @TableName base_attr_info
 */
@TableName(value ="base_attr_info")
@Data
public class BaseAttrInfo implements Serializable {
    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 属性名称
     */
    private String attrName;

    /**
     * 分类id
     */
    private Long categoryId;

    /**
     * 分类层级
     */
    private Integer categoryLevel;

    /**
     * 当前属性的所有值
     */
    @TableField(exist = false) //根据API接口文档 JSON返回给前端，当前属性不是数据库表中的字段
    private List<BaseAttrValue> attrValueList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}