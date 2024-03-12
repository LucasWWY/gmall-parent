package com.example.gmall.service.item.service;

import com.example.gmall.service.product.vo.SkuDetailVO;

import java.lang.reflect.Type;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 22/2/2024 - 1:18 am
 * @Description
 */
public interface CacheService {

    SkuDetailVO getFromCache(Long skuId);

    Boolean mightContain(Long skuId);

    void saveData(Long skuId, Object retVal);

    Object getCacheData(String key, Type returnType);
}
