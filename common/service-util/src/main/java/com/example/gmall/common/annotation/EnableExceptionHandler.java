package com.example.gmall.common.annotation;

import com.example.gmall.common.autoconfig.GlobalExceptionAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 3/4/2024 - 11:08 am
 * @Description
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(GlobalExceptionAutoConfiguration.class)
public @interface EnableExceptionHandler {
}
