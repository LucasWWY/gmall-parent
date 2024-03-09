package com.example.gmall.service.item;

import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 1/3/2024 - 5:32 pm
 * @Description
 */
public class RedissonTest {
    @Test
    void  redissonClient(){
        //1、创建配置
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://192.168.200.100:6379")
                .setPassword("Lfy123!@!");
        //2、创建客户端
        RedissonClient client = Redisson.create(config);
        System.out.println(client);
    }
}

