package com.example.gmall.service.item;

import com.example.gmall.common.config.thread.annotation.EnableMyThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

//@EnableAppCache //更进一步可以使用starter代替，连注解都不需要，类似redis，引入starter即使用
//@EnableAspectJAutoProxy //开启AOP 没有抽取缓存切面逻辑到common之前，在这里切面使用处开启；现在放到CacheAspect/CacheAutoConfiguration中开启

//针对skuDetailInfo -> 针对通用信息 -> 因为更通用了，所以放在service-item下不合理，抽取切面逻辑common -> 更方便的整合成starter
@EnableMyThreadPool //因为MyThreadPoolAutoConfiguration.java在common/service-util模块下，SpringBoot扫描不到，所以需要手动引入@Import
@EnableFeignClients
@SpringBootApplication
public class ServiceItemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceItemApplication.class, args);
    }

}
