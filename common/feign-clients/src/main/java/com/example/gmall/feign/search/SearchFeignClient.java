package com.example.gmall.feign.search;

import com.example.gmall.common.result.Result;
import com.example.gmall.model.search.Goods;
import com.example.gmall.model.search.vo.SearchParamVO;
import com.example.gmall.model.search.vo.SearchRespVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 15/4/2024 - 6:28 am
 * @Description
 */
@RequestMapping("/api/inner/rpc/search")
@FeignClient("search-service")
public interface SearchFeignClient {

    @PostMapping("/searchgoods")
    Result<SearchRespVO> search(@RequestBody SearchParamVO searchParamVo);

    /**
     * 商品上架(launch a product)
     *
     * @return
     */
    @PostMapping("/up/goods")
    Result up(@RequestBody Goods goods);

    /**
     * 商品下架
     *
     * @param skuId
     * @return
     */
    @GetMapping("/down/goods/{skuId}")
    Result down(@PathVariable("skuId") Long skuId);

    @GetMapping("/hotscore/{skuId}/{score}")
    public Result updateHotScore(@PathVariable("skuId") Long skuId,
                                 @PathVariable("score") Long score);
}
