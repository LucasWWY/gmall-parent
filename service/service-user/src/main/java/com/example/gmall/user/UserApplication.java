package com.example.gmall.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 14/5/2024 - 10:35 pm
 * @Description
 */
@MapperScan("com.example.gmall.user.mapper")
@SpringCloudApplication
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}