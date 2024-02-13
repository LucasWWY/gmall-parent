package com.example.gmall.service.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.gmall.common.result.Result;
import com.example.gmall.service.product.entity.SpuImage;
import com.example.gmall.service.product.entity.SpuInfo;
import com.example.gmall.service.product.entity.SpuSaleAttr;
import com.example.gmall.service.product.service.SpuImageService;
import com.example.gmall.service.product.service.SpuInfoService;
import com.example.gmall.service.product.service.SpuSaleAttrService;
import com.example.gmall.service.product.vo.SpuSaveInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 11/2/2024 - 5:00 pm
 * @Description
 */
@Api(tags = "spu管理")
@RestController
@RequestMapping("/admin/product")
public class SpuController {
    @Autowired
    SpuInfoService spuInfoService;

    @Autowired
    SpuImageService spuImageService;

    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    @ApiOperation("SPU分页查询")
    @GetMapping("/{pn}/{ps}")
    public Result getSpu(@RequestParam("category3Id") Long category3Id,
                         @PathVariable("pn") Integer pn,
                         @PathVariable("ps") Integer ps) {

        //Page<SpuInfo> page = spuInfoService.page(new Page<SpuInfo>(pn, ps));
        Page<SpuInfo> page = spuInfoService.lambdaQuery()
                .eq(SpuInfo::getCategory3Id, category3Id)
                .page(new Page<>(pn, ps));

        return Result.ok(page);
    }

    @Transactional
    @ApiOperation("保存SPU")
    @PostMapping("/saveSpuInfo")
    public Result save(@RequestBody SpuSaveInfoVO spuSaveInfoVO) {
        spuInfoService.saveSpuInfoData(spuSaveInfoVO);
        return Result.ok();
    }
    //如果前段传过来的模型不固定，我们可以直接封装成Map
    //public Result save(@RequestBody Map<String, Object> json) {
    //    return Result.ok();
    //}

    @ApiOperation("查询spu的图片列表")
    @GetMapping("/spuImageList/{spuId}")
    public Result getSpuImageList(@PathVariable("spuId") Long spuId){

        List<SpuImage> list = spuImageService.lambdaQuery()
                .eq(SpuImage::getSpuId, spuId)
                .list();

        return Result.ok(list);
    }

    @ApiOperation("查询spu定义的所有销售属性名和值")
    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result getSpuSaleAttrList(@PathVariable("spuId") Long spuId){

        List<SpuSaleAttr> saleAttrs = spuSaleAttrService.getSpuSaleAttrList(spuId); //因为还要查销售属性的值，所以不用lambdaQuery
        return Result.ok(saleAttrs);
    }
}
