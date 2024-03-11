package com.example.gmall.service.item.mybatis.annotation;

import java.lang.annotation.*;

/**
 * @author lfy
 * @Description
 * @create 2022-12-09 9:36
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MySQL {
    String value();
}
