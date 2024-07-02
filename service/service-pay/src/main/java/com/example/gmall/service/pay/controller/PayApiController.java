package com.example.gmall.service.pay.controller;

import com.alipay.api.AlipayApiException;
import com.example.gmall.common.authen.UserAuthUtils;
import com.example.gmall.service.pay.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lfy
 * @Description
 * @create 2022-12-24 9:57
 */
@RequestMapping("/api/payment")
@RestController
public class PayApiController {

    @Autowired
    PayService payService;

    /**
     * 请求二维码收银台页面
     * @param orderId
     * @return
     * @throws
     */
    @GetMapping("/alipay/submit/{orderId}")
    public String alipay(@PathVariable("orderId") Long orderId) throws AlipayApiException {
        Long userId = UserAuthUtils.getUserId();
        String page = payService.generatePayPage(orderId,userId);
        return page; //直接显示二维码收银台
    }
}
