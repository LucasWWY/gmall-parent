package com.example.gmall.service.order;

import com.example.gmall.common.annotation.EnableUserAuthFeignInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author lfy
 * @Description
 * @create 2022-12-21 10:07
 */
//@EnableMqService
//@EnableTransactionManagement
@EnableFeignClients(basePackages = {
        "com.example.gmall.feign.cart",
        "com.example.gmall.feign.product",
        "com.example.gmall.feign.user",
        "com.example.gmall.feign.ware"
})
@EnableUserAuthFeignInterceptor //在远程调用之前 feign拦截器会向requestTemplate中添加用户信息，这样新request就可以携带用户信息到下一个微服务
@MapperScan(basePackages = "com.example.gmall.service.order.mapper")
@SpringCloudApplication
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class,args);
    }
}
