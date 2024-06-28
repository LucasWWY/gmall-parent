package com.example.gmall.service.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.gmall.model.order.entity.PaymentInfo;
import com.example.gmall.service.order.mapper.PaymentInfoMapper;
import com.example.gmall.service.order.service.PaymentInfoService;
import org.springframework.stereotype.Service;

/**
* @author lfy
* @description 针对表【payment_info(支付信息表)】的数据库操作Service实现
* @createDate 2022-12-21 10:10:41
*/
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo>
    implements PaymentInfoService {

}




