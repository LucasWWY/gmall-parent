package com.example.gmall.service.order.biz;

import com.alibaba.fastjson.JSONObject;

/**
 * @author lfy
 * @Description 物流服务
 * @create 2022-12-26 14:31
 */
public interface LogisticService {

    /**
     * 给系统指定的订单生成电子面单
     * @param orderId
     * @param userId
     * @return
     */
    JSONObject generateEOrder(Long orderId, Long userId) throws Exception;

    JSONObject searchLogisticStatus();
}
