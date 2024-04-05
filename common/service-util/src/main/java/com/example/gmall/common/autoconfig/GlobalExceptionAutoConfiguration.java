package com.example.gmall.common.autoconfig;

import com.example.gmall.common.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 3/4/2024 - 11:00 am
 * @Description
 */
@Import(GlobalExceptionHandler.class) //注册GlobalExceptionHandler，或者在方法上使用@Bean
@Configuration
public class GlobalExceptionAutoConfiguration {
}
