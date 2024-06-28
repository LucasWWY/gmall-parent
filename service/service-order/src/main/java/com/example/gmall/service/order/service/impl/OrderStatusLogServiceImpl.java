package com.example.gmall.service.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.gmall.model.order.entity.OrderStatusLog;
import com.example.gmall.service.order.mapper.OrderStatusLogMapper;
import com.example.gmall.service.order.service.OrderStatusLogService;
import org.springframework.stereotype.Service;

/**
* @author lfy
* @description 针对表【order_status_log】的数据库操作Service实现
* @createDate 2022-12-21 10:10:41
*/
@Service
public class OrderStatusLogServiceImpl extends ServiceImpl<OrderStatusLogMapper, OrderStatusLog>
    implements OrderStatusLogService {

}




