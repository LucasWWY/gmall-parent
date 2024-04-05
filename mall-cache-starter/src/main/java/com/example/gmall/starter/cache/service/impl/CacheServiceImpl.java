package com.example.gmall.starter.cache.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.gmall.starter.cache.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 22/2/2024 - 1:19 am
 * @Description
 */
@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public Object getCacheData(String cacheKey, Type returnType) {
        //1. 查询缓存
        String jsonString = redisTemplate.opsForValue().get(cacheKey);

        //缓存没有
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        } else if ("x".equals(jsonString)) {
            return new Object(); //应对缓存穿透的假数据，占个位
        } else {
            //2. 缓存有
            return JSON.parseObject(jsonString, returnType);
        }
    }

    ScheduledExecutorService pool = Executors.newScheduledThreadPool(16);
    @Override
    public void delayDoubleDel(String cacheKey) {
        redisTemplate.delete(cacheKey);

        //CompletableFuture.runAsync(() -> {
        //    try {
        //        TimeUnit.SECONDS.sleep(10); //一般是核心16线程，每个线程执行双删会睡10s，可能阻塞所有线程
        //        redisTemplate.delete(cacheKey);
        //    } catch (InterruptedException e) {
        //        throw new RuntimeException(e);
        //    }
        //});

        pool.schedule(() -> {
            redisTemplate.delete(cacheKey);
        }, 10, TimeUnit.SECONDS); //线程不会睡觉，JVM利用时间片算法进行调用

    }

    @Override
    public Boolean mightContain(String bitMapName, Long bitMapIndex) {
        return redisTemplate.opsForValue().getBit(bitMapName, bitMapIndex);
    }

    @Override
    public void saveCacheData(String cacheKey, Object retVal, long ttl, TimeUnit unit) {
        String jsonString = "x"; //默认假数据

        if (retVal != null) {
            jsonString = JSON.toJSONString(retVal);
        }

        redisTemplate.opsForValue().set(cacheKey, jsonString, ttl, unit);
    }


}
