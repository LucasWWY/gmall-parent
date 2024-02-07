package com.example.gmall.service.product;

import com.example.gmall.common.config.Swagger2Config;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.Import;

//@EnableCircuitBreaker
//@EnableDiscoveryClient
//@SpringBootApplication
@Import({Swagger2Config.class}) //启动只扫描主程序所在的包以及子包
@MapperScan("com.example.gmall.service.product.mapper")
@SpringCloudApplication
public class ServiceProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceProductApplication.class, args);
    }

}
