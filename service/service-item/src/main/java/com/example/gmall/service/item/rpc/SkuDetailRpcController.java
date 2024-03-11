package com.example.gmall.service.item.rpc;

import com.example.gmall.common.result.Result;
import com.example.gmall.service.item.service.SkuDetailService;
import com.example.gmall.service.product.vo.SkuDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 15/2/2024 - 3:58 pm
 * @Description
 */
@RequestMapping("/api/inner/rpc/item")
@RestController
public class SkuDetailRpcController {

    @Autowired
    SkuDetailService skuDetailService;

    @GetMapping("/sku/detail/{skuId}")
    public Result<SkuDetailVO> getSkuDetails(@PathVariable("skuId") Long skuId){
        //切面拦截
        SkuDetailVO sKuDetailVO = skuDetailService.getSkuDetailData(skuId);

        return Result.ok(sKuDetailVO);
    }
}
