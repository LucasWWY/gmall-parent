package com.example.gmall.model.mq.ware;

import lombok.Data;

import java.util.List;

/**
 * @author lfy
 * @Description
 * @create 2022-12-26 9:11
 */
@Data
public class WareStockMsg {

    private Long orderId;
    private Long userId;
    private String consignee;
    private String consigneeTel;
    private String orderComment;
    private String orderBody;
    private String deliveryAddress;
    private String paymentWay;
    private Long wareId;

    private List<Sku>  details;

    @Data
    public static class Sku {
        private Long skuId;
        private Integer skuNum;
        private String skuName;
    }

}
