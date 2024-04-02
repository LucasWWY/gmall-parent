//package com.example.gmall.common.autoconfig;
//
//import com.example.gmall.common.annotation.EnableRedisson;
//import com.example.gmall.common.cache.aspect.CacheAspect;
//import com.example.gmall.common.cache.service.CacheService;
//import com.example.gmall.common.cache.service.impl.CacheServiceImpl;
//import org.springframework.boot.autoconfigure.AutoConfigureAfter;
//import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @author Lucas (Weiye) Wang
// * @version 1.0.0
// * @date 2/4/2024 - 2:26 pm
// * @Description
// */
////@Import() //和@Bean类似
//@EnableRedisson //RedissonAutoconfiguration需要手动引入
//@AutoConfigureAfter({RedisAutoConfiguration.class, RedissonAutoConfiguration.class}) //需要在redisTemplate配置后才能注册CacheService bean, CacheAspect还需要注入RedissonClient
//@Configuration
//public class CacheAutoConfiguration {
//
//    @Bean //想要让所有module都能使用cache切面，需要将cacheAspect加入到容器中，但是其他module只能扫描main方法所在的包，所以需要使用自动配置类
//    public CacheAspect cacheAspect() {
//        return new CacheAspect();
//    }
//
//    @Bean
//    public CacheService cacheService() {
//        return new CacheServiceImpl(); //需要注入redisTemplate，所以还要要在redisTemplate配置后才能注册CacheService bean
//    }
//}
