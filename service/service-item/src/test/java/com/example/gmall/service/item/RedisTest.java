package com.example.gmall.service.item;

import com.alibaba.fastjson.JSON;
import com.example.gmall.model.product.entity.SkuImage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.UUID;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 21/2/2024 - 12:57 am
 * @Description
 */
@SpringBootTest
public class RedisTest {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    void testJson(){
        SkuImage image = null;
        image.setId(0L);
        image.setSkuId(0L);
        image.setImgName("a");
        image.setImgUrl("a");
        image.setSpuImgId(0L);
        image.setIsDefault("1");


        //转json
        String jsonString = JSON.toJSONString(image);
        System.out.println(jsonString);
    }

    @Test
    void testRedis(){
        redisTemplate.opsForValue().set("a", UUID.randomUUID().toString());
        System.out.println("保存完成...");

        String a = redisTemplate.opsForValue().get("a");
        System.out.println("读到："+a);
    }
}

