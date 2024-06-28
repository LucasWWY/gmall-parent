package com.example.gmall.service.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.gmall.model.order.entity.OrderInfo;

/**
* @author lfy
* @description 针对表【order_info(订单表 订单表)】的数据库操作Service
* @createDate 2022-12-21 10:10:41
*/
public interface OrderInfoService extends IService<OrderInfo> {

    OrderInfo getOrderInfoByIdAndUserId(Long id, Long userId);
}
