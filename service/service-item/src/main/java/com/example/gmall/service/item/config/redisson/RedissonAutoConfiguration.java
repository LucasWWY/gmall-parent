package com.example.gmall.service.item.config.redisson;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 1/3/2024 - 5:43 pm
 * @Description
 */
@AutoConfigureAfter(RedisAutoConfiguration.class) //因为需要使用到RedisProperties，所以在RedisAutoConfiguration的@EnableConfigurationProperties开启绑定之后
//@EnableConfigurationProperties(RedisProperties.class) //开启配置绑定
@Configuration
public class RedissonAutoConfiguration {

    @Bean
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        //1、创建配置
        Config config = new Config();
        config.useSingleServer() // 单机模式
                .setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort());

        //2、创建客户端
        RedissonClient client = Redisson.create(config);
        return client;
    }
}
