package com.example.gmall.service.item.aspect.annotation;

import java.lang.annotation.*;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 11/3/2024 - 4:27 pm
 * @Description
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface MallCache {

    String key() default "";

}
