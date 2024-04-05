package com.example.gmall.service.item;

import com.example.gmall.common.config.thread.annotation.EnableMyThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


//@EnableAspectJAutoProxy //开启AOP 没有抽取缓存切面逻辑到common之前，在这里切面使用处开启；现在放到CacheAspect/CacheAutoConfiguration中开启
//@EnableAppCache //更进一步可以使用starter代替，连注解都不需要，类似使用redis，引入starter即可使用

//针对skuDetailInfo -> 针对通用信息 -> 因为更通用了，所以放在service-item下不合理，抽取切面逻辑common -> 更方便的整合成starter
@EnableMyThreadPool //因为MyThreadPoolAutoConfiguration在common/service-util模块下，当前模块的SB扫描不到，所以需要手动引入@Import
//在Spring Cloud项目中，每个微服务通常被视为独立的应用，这意味着每个微服务模块都会有自己的启动类和Spring应用上下文。这与传统的Spring Boot多模块项目有所不同，在那种项目中，通常只有一个启动类和共享的Spring应用上下文。
@EnableFeignClients(basePackages = {"com.example.gmall.feign.product"}) //注意：如果引入com.example.gmall.feign，那么feign/item/SkuDetailFeignClient中的方法就会和item/rpc/SkuDetailRpcController中的方法冲突，二者url和方法名都会重复，因为feignclient本身就是在封装rpc的方法供别人远程调用
@SpringBootApplication
public class ServiceItemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceItemApplication.class, args);
    }

}
