package com.example.gmall.service.item.service;

import com.example.gmall.service.product.vo.SkuDetailVO;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 15/2/2024 - 4:07 pm
 * @Description
 */
public interface SkuDetailService {
    SkuDetailVO getSkuDetailData(Long skuId);
}
