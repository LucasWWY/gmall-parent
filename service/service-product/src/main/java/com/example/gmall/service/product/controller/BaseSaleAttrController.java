package com.example.gmall.service.product.controller;

import com.example.gmall.common.result.Result;
import com.example.gmall.model.product.entity.BaseSaleAttr;
import com.example.gmall.service.product.service.BaseSaleAttrService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 13/2/2024 - 5:04 am
 * @Description
 */
@Api(tags = "销售属性")
@RequestMapping("/admin/product")
@RestController
public class BaseSaleAttrController {

    @Autowired
    BaseSaleAttrService baseSaleAttrService;

    @GetMapping("/baseSaleAttrList")
    public Result getSaleAttrList() {
        List<BaseSaleAttr> list = baseSaleAttrService.list();
        return Result.ok(list);
    }

}
