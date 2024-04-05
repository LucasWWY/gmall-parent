package com.example.gmall.weball.controller;

import com.example.gmall.common.result.Result;
import com.example.gmall.feign.item.ItemSkuDetailFeignClient;
import com.example.gmall.feign.product.ProductSkuDetailFeignClient;
import com.example.gmall.service.product.vo.SkuDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 15/2/2024 - 1:43 am
 * @Description
 */
@RequestMapping("/item")
@Controller
public class ItemController {

    @Autowired
    ItemSkuDetailFeignClient itemSkuDetailFeignClient;

    @Autowired
    ProductSkuDetailFeignClient productSkuDetailFeignClient;

    /**
     * 商品详情页
     * @param skuId
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String itemPage(@PathVariable("skuId") Long skuId, Model model){

        //远程调用查询详情数据
        Result<SkuDetailVO> skuDetails = itemSkuDetailFeignClient.getSkuDetails(skuId);
        SkuDetailVO skuDetailVO = skuDetails.getData();

        //1、分类视图 {category1Id、category2Id、category3Id、category1Name、category2Name、category3Name}
        model.addAttribute("categoryView",skuDetailVO.getCategoryView());

        //2、sku信息 {基本信息、图片列表}
        model.addAttribute("skuInfo",skuDetailVO.getSkuInfo());

        //3、实时价格
        //BigDecimal bigDecimal = productSkuDetailFeignClient.getPrice(skuId).getData();
        //model.addAttribute("price",bigDecimal);
        BigDecimal price = productSkuDetailFeignClient.getPrice(skuId).getData(); //不用担心缓存
        model.addAttribute("price",price);

        //4、所有销售属性集合
        model.addAttribute("spuSaleAttrList",skuDetailVO.getSpuSaleAttrList());

        //5、valuesSkuJson
        model.addAttribute("valuesSkuJson",skuDetailVO.getValuesSkuJson());

        return "item/index";
    }

}
