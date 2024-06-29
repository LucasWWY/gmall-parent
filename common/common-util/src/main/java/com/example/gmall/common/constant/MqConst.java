package com.example.gmall.common.constant;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 30/6/2024 - 12:56 am
 * @Description
 */
public class MqConst {

    public static final String ORDER_EVENT_EXCHANGE = "order-event-exchange";
    public static final String ORDER_DELAY_QUEUE = "order-delay-queue";
    public static final String  ORDER_TIMEOUT_RK = "order.timeout";
    public static final String ORDER_CREATE_RK = "order.create";
    public static final String ORDER_PAYED_RK = "order.payed";
    public static final String ORDER_LOGISTIC_RK = "order.logistic";
    public static final String ORDER_DEAD_QUEUE = "order-dead-queue";
    public static final String ORDER_LOGISTIC_QUEUE = "order-logistic-queue";
    public static final Long ORDER_TTL = 30*60*1000L;
    public static final String ORDER_PAYED_QUEUE = "order-payed-queue";
    public static final String WARE_STOCK_EXCHANGE = "exchange.direct.ware.stock";
    public static final String WARE_STOCK_RK = "ware.stock";
    public static final String SECKILL_EVENT_EXCHANGE = "seckill-event-exchange";

    public static final String SECKILL_ORDER_RK = "seckill.order";
    public static final String SECKILL_ORDER_QUEUE = "seckill-order-queue";
}

