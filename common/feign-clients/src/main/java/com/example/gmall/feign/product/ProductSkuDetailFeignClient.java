package com.example.gmall.feign.product;

import com.example.gmall.common.result.Result;
import com.example.gmall.model.product.entity.SkuImage;
import com.example.gmall.model.product.entity.SkuInfo;
import com.example.gmall.model.product.entity.SpuSaleAttr;
import com.example.gmall.model.product.vo.CategoryTreeVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 15/2/2024 - 5:17 pm
 * @Description
 * 3/4/2024: 抽取到common下的feign-clients模块
 */
@RequestMapping("/api/inner/rpc/product")
@FeignClient("service-product ")
public interface ProductSkuDetailFeignClient {

    /**
     * 给sku详情(web-all -> service-item -> service-product)使用的，每个sku必有三级分类
     * 1. 根据三级分类id，得到整个分类的完整路径
     *
     * @param c3Id
     * @return
     */
    @GetMapping("/category/view/{c3Id}")
    Result<CategoryTreeVO> getCategoryTreeWithC3Id(@PathVariable("c3Id") Long c3Id);

    /**
     * 2. 获取sku-info
     *
     * @param skuId
     * @return
     */
    @GetMapping("/skuinfo/{skuId}")
    Result<SkuInfo> getSkuInfo(@PathVariable("skuId") Long skuId);

    /**
     * 3. 获取sku-images
     *
     * @param skuId
     * @return
     */
    @GetMapping("/skuimages/{skuId}")
    Result<List<SkuImage>> getSkuImages(@PathVariable("skuId") Long skuId);

    /**
     * 4. 获取 实时价格
     *
     * @param skuId
     * @return
     */
    @GetMapping("/skuprice/{skuId}")
    Result<BigDecimal> getPrice(@PathVariable("skuId") Long skuId);


    /**
     * 5. 获取 spu销售属性名和值 集合
     * @param spuId
     * @param skuId
     * @return
     */
    @GetMapping("/skusaleattrttr/{spuId}/{skuId}")
    Result<List<SpuSaleAttr>> getSpuSaleAttr(@PathVariable("spuId") Long spuId, @PathVariable("skuId") Long skuId);

    /**
     * 6、获取 valuesSkuJson
     *
     * @param spuId
     * @return
     */
    @GetMapping("/valuesSkuJson/{spuId}")
    public Result<String> getValuesSkuJson(@PathVariable("spuId") Long spuId);
}
