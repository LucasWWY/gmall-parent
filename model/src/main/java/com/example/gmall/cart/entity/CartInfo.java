package com.example.gmall.cart.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 22/6/2024 - 9:08 pm
 * @Description
 */
@Data
public class CartInfo implements Serializable {
    /**
     * skuid
     */
    private Long skuId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 商品第一次放入购物车时价格;
     */
    private BigDecimal cartPrice;

    /**
     * 商品的实时价格，和cartPrice比较 显示降价多少
     */
    private BigDecimal skuPrice;

    /**
     * 数量
     */
    private Integer skuNum;

    /**
     * 图片文件
     */
    private String imgUrl;

    /**
     * sku名称 (冗余)
     */
    private String skuName;


    private Integer isChecked;


    private Date createTime;


    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
