package com.example.gmall.service.pay.service;

import com.alipay.api.AlipayApiException;

/**
 * @author lfy
 * @Description
 * @create 2022-12-24 11:20
 */
public interface PayService {

    /**
     * 生成支付页
     * @param orderId
     * @param userId
     * @return
     */
    String generatePayPage(Long orderId, Long userId) throws AlipayApiException;
}
