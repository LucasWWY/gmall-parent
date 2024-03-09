package com.example.gmall.service.item;

import com.example.gmall.common.config.thread.annotation.EnableMyThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableMyThreadPool //因为MyThreadPoolAutoConfiguration.java在common/service-util模块下，SpringBoot扫描不到，所以需要手动引入@Import
@EnableFeignClients
@SpringBootApplication
public class ServiceItemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceItemApplication.class, args);
    }

}
