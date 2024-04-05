package com.example.gmall.weball;

import com.example.gmall.common.annotation.EnableExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableExceptionHandler
@SpringCloudApplication
@EnableFeignClients(basePackages = {
        "com.example.gmall.feign.product",
        "com.example.gmall.feign.item"
})
//在一个典型的多模块Spring Boot项目中，虽然各个模块（子项目）可能被组织在不同的包下，但通常都会共享同一个Spring应用上下文。
//这是因为整个Spring Boot应用启动时，通常只会有一个启动类（带有@SpringBootApplication注解的类），通常位于项目的根模块或主模块中。
//这个启动类负责初始化Spring应用上下文，整个项目共享一个Spring应用上下文，并触发组件扫描。如果根模块没有包含一个启动类，那么项目的启动和组件扫描就依赖于子模块中的启动类。
//在Spring Cloud项目中，每个微服务通常被视为独立的应用，这意味着每个微服务模块都会有自己的启动类和Spring应用上下文。这与传统的Spring Boot多模块项目有所不同，在那种项目中，通常只有一个启动类和共享的Spring应用上下文。
public class WebAllApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebAllApplication.class, args);
    }

}
