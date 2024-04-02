package com.example.gmall.service.item;

import com.example.gmall.common.annotation.EnableAppCache;
import com.example.gmall.common.config.thread.annotation.EnableMyThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAppCache //更进一步可以使用starter代替，连注解都不需要，类似redis，引入starter即使用
@EnableAspectJAutoProxy //开启AOP
@EnableMyThreadPool //因为MyThreadPoolAutoConfiguration.java在common/service-util模块下，SpringBoot扫描不到，所以需要手动引入@Import
@EnableFeignClients
@SpringBootApplication
public class ServiceItemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceItemApplication.class, args);
    }

}
