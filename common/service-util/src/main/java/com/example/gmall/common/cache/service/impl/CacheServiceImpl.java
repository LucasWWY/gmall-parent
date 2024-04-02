package com.example.gmall.common.cache.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.gmall.common.cache.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
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
    public Object getCacheData(String key, Type returnType) {
        //1. 查询缓存
        String jsonString = redisTemplate.opsForValue().get(key);

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
