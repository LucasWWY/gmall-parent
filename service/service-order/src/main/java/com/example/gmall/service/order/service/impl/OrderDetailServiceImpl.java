package com.example.gmall.service.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.gmall.model.order.entity.OrderDetail;
import com.example.gmall.service.order.mapper.OrderDetailMapper;
import com.example.gmall.service.order.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
* @author lfy
* @description 针对表【order_detail(订单明细表)】的数据库操作Service实现
* @createDate 2022-12-21 10:10:41
*/
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>
    implements OrderDetailService {

}




