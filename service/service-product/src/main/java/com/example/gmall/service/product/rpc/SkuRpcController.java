package com.example.gmall.service.product.rpc;

import com.example.gmall.common.result.Result;
import com.example.gmall.model.product.entity.SkuImage;
import com.example.gmall.model.product.entity.SkuInfo;
import com.example.gmall.model.product.entity.SpuSaleAttr;
import com.example.gmall.service.product.service.SkuImageService;
import com.example.gmall.service.product.service.SkuInfoService;
import com.example.gmall.service.product.service.SpuSaleAttrService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 15/2/2024 - 5:23 pm
 * @Description
 */
@Slf4j
@RequestMapping("/api/inner/rpc/product")
@RestController
public class SkuRpcController {

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImageService skuImageService;

    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    /**
     * 1、获取sku-info
     * @param skuId
     * @return
     */
    @GetMapping("/skuinfo/{skuId}")
    public Result<SkuInfo> getSkuInfo(@PathVariable("skuId") Long skuId){
        SkuInfo skuInfo = skuInfoService.getById(skuId);
        return Result.ok(skuInfo);
    }

    /**
     * 2、获取sku-images
     * @param skuId
     * @return
     */
    @GetMapping("/skuimages/{skuId}")
    public Result<List<SkuImage>> getSkuImages(@PathVariable("skuId") Long skuId){
        List<SkuImage> skuImages = skuImageService.lambdaQuery()
                .eq(SkuImage::getSkuId, skuId)
                .list();
        return Result.ok(skuImages);
    }

    /**
     * 3、获取 实时价格
     * @param skuId
     * @return
     */
    @GetMapping("/skuprice/{skuId}")
    public Result<BigDecimal> getPrice(@PathVariable("skuId") Long skuId){
        //select price from sku_info where id=49
        //减小网络传输的数据量，假如 网络带宽1MB/s，那么1kb传输，并发量可以达到1000，越小并发量越多
        SkuInfo one = skuInfoService.lambdaQuery()
                .select(SkuInfo::getPrice) //只查找指定列
                .eq(SkuInfo::getId, skuId)
                .one(); //返回的对象只封装了price，因为只select了price

        return Result.ok(one.getPrice());
    }

    /**
     * 4、获取 spu销售属性名和值 集合
     * @param spuId
     * @return
     */
    @GetMapping("/skusaleattr/{spuId}/{skuId}")
    public Result<List<SpuSaleAttr>> getSpuSaleAttr(@PathVariable("spuId") Long spuId, @PathVariable("skuId") Long skuId){

        List<SpuSaleAttr>  saleAttrs = spuSaleAttrService.getSpuSaleAttrListOrder(spuId, skuId);

        return Result.ok(saleAttrs);
    }

    /**
     * 5、获取 valuesSkuJson
     * @param spuId
     * @return
     */
    @GetMapping("/valuesSkuJson/{spuId}")
    public Result<String> getValuesSkuJson(@PathVariable("spuId") Long spuId){

        String json =  spuSaleAttrService.getValuesSkuJson(spuId);



        return Result.ok(json);
    }

}
