package com.example.gmall.service.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.gmall.common.result.Result;
import com.example.gmall.service.product.entity.SkuInfo;
import com.example.gmall.service.product.service.SkuInfoService;
import com.example.gmall.service.product.vo.SkuSaveInfoVO;
import com.example.gmall.starter.cache.service.CacheService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 14/2/2024 - 12:35 am
 * @Description
 */
@RestController
@RequestMapping("/admin/product")
public class SkuController {

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    CacheService cacheService;

    //修改Sku
    @GetMapping("/updateSkuInfo")
    public Result updateSkuInfo(@RequestBody SkuSaveInfoVO vo){
        //1、修改数据库

        //2、删除缓存
        cacheService.delayDoubleDel("sku:info:49");

        return Result.ok();
    }

    @ApiOperation("sku分页列表")
    @GetMapping("/list/{page}/{limit}")
    public Result skuList(@PathVariable("page") Long pn,
                          @PathVariable("limit") Long ps){

        Page<SkuInfo> page = skuInfoService.page(new Page<SkuInfo>(pn, ps));
        return Result.ok(page);
    }

    @ApiOperation("保存sku")
    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuSaveInfoVO skuSaveInfoVO){
        skuInfoService.saveSkuInfoData(skuSaveInfoVO);
        //爬虫：拿到商品，录制到数据库？
        return Result.ok();
    }

    //商品上架：在商家后台，商品已经存在，点击“上架”按钮，上架以后，也要存到ES
    @GetMapping("/onSale/{skuId}")
    public Result up(@PathVariable("skuId") Long skuId){
        skuInfoService.upGoods(skuId);
        return Result.ok();
    }

    //下架
    @GetMapping("/cancelSale/{skuId}")
    public Result down(@PathVariable("skuId") Long skuId){
        skuInfoService.downGoods(skuId);
        return Result.ok();
    }

}
