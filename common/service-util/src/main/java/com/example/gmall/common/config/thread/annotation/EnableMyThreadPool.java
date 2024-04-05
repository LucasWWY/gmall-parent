package com.example.gmall.common.config.thread.annotation;

import com.example.gmall.common.autoconfig.MyThreadPoolAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 19/2/2024 - 7:34 pm
 * @Description
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({MyThreadPoolAutoConfiguration.class}) //因为使用自定义线程池是service/service-item模块，微服务自成体系，其SpringBoot扫描不到自动配置，所以需要手动引入@Import
public @interface EnableMyThreadPool {
}
