package com.example.gmall.service.pay;

import com.example.gmall.common.annotation.EnableMqService;
import com.example.gmall.common.annotation.EnableUserAuthFeignInterceptor;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 30/6/2024 - 4:59 am
 * @Description
 */
@EnableMqService
@EnableUserAuthFeignInterceptor //在远程调用之前 feign拦截器会向requestTemplate中添加用户信息，这样新request就可以携带用户信息到下一个微服务
//为什么有了requestContextHolder还要这个拦截器呢？因为requestContextHolder无法跨微服务
//generatePayPage中OrderInfo orderInfo = orderFeignClient.getOrderInfoById(orderId).getData();需要隐式透传userId
@EnableFeignClients(basePackages = "com.example.gmall.feign.order")
@SpringCloudApplication
public class PaymentApplication {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}