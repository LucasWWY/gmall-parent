package com.example.gmall.cart.vo;

import com.example.gmall.service.product.entity.SkuInfo;
import lombok.Data;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 20/6/2024 - 10:23 pm
 * @Description
 */
@Data
public class AddCartSuccessVO {

    private SkuInfo skuInfo;

    private Integer skuNum;
}
