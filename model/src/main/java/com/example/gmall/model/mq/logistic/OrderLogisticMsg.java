package com.example.gmall.model.mq.logistic;

import lombok.Data;

/**
 * @author lfy
 * @Description
 * @create 2022-12-26 14:16
 */
@Data
public class OrderLogisticMsg {
    private Long orderId;
    private Long userId;
}
