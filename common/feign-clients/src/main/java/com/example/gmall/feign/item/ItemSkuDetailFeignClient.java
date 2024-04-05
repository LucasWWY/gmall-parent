package com.example.gmall.feign.item;


import com.example.gmall.common.result.Result;
import com.example.gmall.service.product.vo.SkuDetailVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 15/2/2024 - 4:02 pm
 * @Description
 * 3/4/2024: 抽取到common下的feign-clients模块
 */
@RequestMapping("/api/inner/rpc/item")
@FeignClient("service-item")
public interface ItemSkuDetailFeignClient {
    @GetMapping("/sku/detail/{skuId}")
    Result<SkuDetailVO> getSkuDetails(@PathVariable("skuId") Long skuId);
}
