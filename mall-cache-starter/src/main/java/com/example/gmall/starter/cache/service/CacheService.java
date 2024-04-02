package com.example.gmall.starter.cache.service;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 22/2/2024 - 1:18 am
 * @Description
 */
public interface CacheService {

    /**
     * 判位图中某位置有没有数据
     * @param bitMapName
     * @param bitMapIndex
     * @return
     */
    Boolean mightContain(String bitMapName, Long bitMapIndex);

    void saveCacheData(String skuId, Object retVal, long ttl, TimeUnit unit);

    Object getCacheData(String key, Type returnType);
}
