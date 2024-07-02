package com.example.gmall.service.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.example.gmall.common.util.DateUtil;
import com.example.gmall.feign.order.OrderFeignClient;
import com.example.gmall.model.order.entity.OrderInfo;
import com.example.gmall.service.pay.config.properties.AlipayProperties;
import com.example.gmall.service.pay.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lfy
 * @Description
 * @create 2022-12-24 11:20
 */
@Service
public class PayServiceImpl implements PayService {

    @Autowired
    OrderFeignClient orderFeignClient;

    @Autowired
    AlipayClient alipayClient;

    @Autowired
    AlipayProperties alipayProperties;

    @Override
    public String generatePayPage(Long orderId, Long userId) throws AlipayApiException {
        //1、创建一个 AlipayClient

        //2、创建一个支付请求
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();

        //3、设置参数
        alipayRequest.setReturnUrl(alipayProperties.getReturn_url()); //同步回调：支付成功以后，浏览器要跳转到的页面地址
        alipayRequest.setNotifyUrl(alipayProperties.getNotify_url()); //通知回调：支付成功以后，支付消息会通知给这个地址

        //4、准备待支付的订单数据
        //远程调用订单服务，获取订单的基本数据，基于此数据构造一个支付页
        OrderInfo orderInfo = orderFeignClient.getOrderInfoById(orderId).getData();

        //商户订单号（对外交易号）
        String outTradeNo = orderInfo.getOutTradeNo();
        //付款金额
        BigDecimal totalAmount = orderInfo.getTotalAmount();
        //订单名称
        String orderName = "gmall-order-"+outTradeNo;
        //商品描述，可空
        String tradeBody = orderInfo.getTradeBody();

        //详细参考：https://opendocs.alipay.com/open/028r8t?scene=22
        //业务参数
        Map<String,Object> bizContent = new HashMap<>();
        bizContent.put("out_trade_no",outTradeNo);
        bizContent.put("total_amount",totalAmount);
        bizContent.put("subject",orderName);
        bizContent.put("body",tradeBody);
        bizContent.put("product_code","FAST_INSTANT_TRADE_PAY");

        //自动关单
        String date = DateUtil.formatDate(orderInfo.getExpireTime(), "yyyy-MM-dd HH:mm:ss");
        bizContent.put("time_expire",date);
        alipayRequest.setBizContent(JSON.toJSONString(bizContent));

        //生成支付页面
        String page = alipayClient.pageExecute(alipayRequest).getBody();

        return page;
    }
}
