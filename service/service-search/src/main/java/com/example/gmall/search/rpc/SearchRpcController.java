package com.example.gmall.search.rpc;

import com.example.gmall.common.result.Result;
import com.example.gmall.model.search.Goods;
import com.example.gmall.search.service.SearchService;
import com.example.gmall.model.search.vo.SearchParamVO;
import com.example.gmall.model.search.vo.SearchRespVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 15/4/2024 - 6:17 am
 * @Description
 */
@RequestMapping("/api/inner/rpc/search")
@RestController
public class SearchRpcController {

    @Autowired
    SearchService searchService;

    /**
     * 检索商品
     * @param searchParamVo
     * @return
     */
    @PostMapping("/searchgoods")
    public Result<SearchRespVO> search(@RequestBody SearchParamVO searchParamVo){
        //检索
        SearchRespVO resp = searchService.search(searchParamVo);
        return Result.ok(resp);
    }

    /**
     * 商品上架(launch a product)
     * @return
     */
    @PostMapping("/up/goods")
    public Result up(@RequestBody Goods goods){
        searchService.up(goods);
        return Result.ok();
    }

    /**
     * 商品下架
     * @param skuId
     * @return
     */
    @GetMapping("/down/goods/{skuId}")
    public Result down(@PathVariable("skuId") Long skuId){
        searchService.down(skuId);
        return Result.ok();
    }

    /**
     * 增加热度分
     * @param skuId
     * @param score
     * @return
     */
    @GetMapping("/hotscore/{skuId}/{score}")
    public Result updateHotScore(@PathVariable("skuId") Long skuId,
                                 @PathVariable("score") Long score){

        searchService.updateHotScore(skuId,score);
        return Result.ok();
    }
}

