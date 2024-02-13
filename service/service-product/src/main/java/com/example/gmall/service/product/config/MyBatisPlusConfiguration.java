package com.example.gmall.service.product.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 13/2/2024 - 7:51 pm
 * @Description
 */
@EnableTransactionManagement
@Configuration
public class MyBatisPlusConfiguration {

    @Bean
    MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        //分页拦截器
        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor();
        pagination.setOverflow(true);

        interceptor.addInnerInterceptor(pagination);
        return interceptor;
    }
}
