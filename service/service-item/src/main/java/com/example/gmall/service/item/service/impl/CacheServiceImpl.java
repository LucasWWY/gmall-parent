package com.example.gmall.service.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.gmall.common.constant.RedisConst;
import com.example.gmall.service.item.service.CacheService;
import com.example.gmall.service.product.vo.SkuDetailVO;
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
    public SkuDetailVO getFromCache(Long skuId) {
        //1. 查询缓存
        String jsonString = redisTemplate.opsForValue().get(RedisConst.SKU_DETAIL_CACHE + skuId);

        //缓存没有
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        } else if ("x".equals(jsonString)) {
            return new SkuDetailVO(); //应对缓存穿透的假数据
        } else {
            //2. 缓存有
            SkuDetailVO skuDetailVO = JSON.parseObject(jsonString, SkuDetailVO.class);
            return skuDetailVO;
        }
    }

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
            SkuDetailVO skuDetailVO = JSON.parseObject(jsonString, returnType);
            return skuDetailVO;
        }
    }

    @Override
    public Boolean mightContain(Long skuId) {
        return redisTemplate.opsForValue().getBit(RedisConst.SKU_DETAIL_CACHE, skuId);
    }

    @Override
    public void saveData(Long skuId, Object retVal) {
        String jsonString = "x"; //默认假数据

        if (retVal != null) {
            jsonString = JSON.toJSONString(retVal);
        }

        redisTemplate.opsForValue().set(RedisConst.SKU_DETAIL_CACHE + skuId, JSON.toJSONString(retVal), 7, TimeUnit.DAYS);
    }


}
