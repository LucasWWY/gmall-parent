package com.example.gmall.model.order.vo;

import com.example.gmall.model.user.entity.UserAddress;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author lfy
 * @Description
 * @create 2022-12-21 14:07
 */
@Data
public class OrderConfirmRespVO {
    //商品列表
    private List<SkuDetail> detailArrayList;
    //总数量
    private Integer totalNum;
    //订单总金额
    private BigDecimal totalAmount;
    //用户收货地址列表
    private List<UserAddress> userAddressList;
    //流水号
    private String tradeNo;

    @Data
    public static class SkuDetail {
        //skuId、imgUrl、skuName、orderPrice、skuNum
        private Long skuId;
        private String imgUrl;
        private String skuName;
        private BigDecimal orderPrice;
        private Integer skuNum;
        private String hasStock = "1"; //1：有货  0：无货
    }
}
