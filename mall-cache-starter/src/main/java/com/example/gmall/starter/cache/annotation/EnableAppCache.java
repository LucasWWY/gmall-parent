package com.example.gmall.starter.cache.annotation;


import com.example.gmall.starter.cache.CacheAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 2/4/2024 - 3:58 pm
 * @Description
 */
@Import(CacheAutoConfiguration.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface EnableAppCache {
}
