package com.example.gmall.common.result;

import lombok.Getter;

/**
 * 统一返回结果状态信息类
 *
 */
@Getter
public enum ResultCodeEnum {

    SUCCESS(200,"成功"),
    FAIL(201, "失败"),
    SERVICE_ERROR(2012, "服务异常"),

    PAY_RUN(205, "支付中"),

    LOGIN_AUTH(208, "未登陆"),
    LOGIN_ERROR(2081, "账号密码错误"),
    PERMISSION(209, "没有权限"),
    INVALID_PARAM(30001,"非法的参数"),
    REPEAT_REQUEST(30002,"多次重复请求，请稍后再试"),
    NO_STOCK(30003,"商品无库存"),
    PRICE_CHANGE(30004,"价格变化"),

    CART_ITEM_NUM_OVERFLOW(222, "单个商品数量不能超过200"),
    CART_ITEM_COUNT_OVERFLOW(223, "购物车商品总数不能超过200"),

    SECKILL_NO_START(210, "秒杀还没开始"),
    SECKILL_RUN(211, "正在排队中"),
    SECKILL_NO_PAY_ORDER(212, "您有未支付的订单"),
    SECKILL_FINISH(213, "已售罄"),
    SECKILL_END(214, "秒杀已结束"),
    SECKILL_SUCCESS(215, "抢单成功"),
    SECKILL_FAIL(216, "抢单失败"),
    SECKILL_ILLEGAL(217, "请求不合法"),
    SECKILL_ORDER_SUCCESS(218, "下单成功"),
    COUPON_GET(220, "优惠券已经领取"),
    COUPON_LIMIT_GET(221, "优惠券已发放完毕");

    private Integer code;

    private String message;

    private ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
