package com.example.gmall.common.annotation;

import com.example.gmall.common.interceptor.requestHeaderUserAuthFeignInterceptor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 23/6/2024 - 10:16 pm
 * @Description
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(requestHeaderUserAuthFeignInterceptor.class)
@Inherited
@Documented
public @interface EnableUserAuthFeignInterceptor {
}
