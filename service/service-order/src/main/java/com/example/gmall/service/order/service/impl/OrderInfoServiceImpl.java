package com.example.gmall.service.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.gmall.model.order.entity.OrderInfo;
import com.example.gmall.service.order.mapper.OrderInfoMapper;
import com.example.gmall.service.order.service.OrderInfoService;
import org.springframework.stereotype.Service;

/**
* @author lfy
* @description 针对表【order_info(订单表 订单表)】的数据库操作Service实现
* @createDate 2022-12-21 10:10:41
*/
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo>
    implements OrderInfoService{

    @Override
    public OrderInfo getOrderInfoByIdAndUserId(Long id, Long userId) {
        OrderInfo orderInfo = lambdaQuery()
                .eq(OrderInfo::getUserId, userId)
                .eq(OrderInfo::getId, id)
                .one();
        //eq顺序不影响查询速度，这里只是sql构建；MySQL 查询优化器会自动调整条件的顺序
        return orderInfo;
        //有时候orderInfo返回null，可能是主从不同步的问题，读都是去从库
    }
}




