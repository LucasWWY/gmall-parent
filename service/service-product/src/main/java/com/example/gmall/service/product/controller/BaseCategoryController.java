package com.example.gmall.service.product.controller;

import com.example.gmall.common.result.Result;
import com.example.gmall.service.product.entity.BaseCategory1;
import com.example.gmall.service.product.entity.BaseCategory2;
import com.example.gmall.service.product.entity.BaseCategory3;
import com.example.gmall.service.product.service.BaseCategory1Service;
import com.example.gmall.service.product.service.BaseCategory2Service;
import com.example.gmall.service.product.service.BaseCategory3Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 6/2/2024 - 6:26 pm
 * @Description
 */
@Api(tags = "三级分类管理")
@RestController
@RequestMapping("/admin/product")
public class BaseCategoryController {

    @Autowired
    BaseCategory1Service baseCategory1Service;

    @Autowired
    BaseCategory2Service baseCategory2Service;

    @Autowired
    BaseCategory3Service baseCategory3Service;



    @ApiOperation("获取所有 一级分类")
    @GetMapping("/getCategory1")
    public Result getCategory1(){
        List<BaseCategory1> category1s = baseCategory1Service.list();
        return Result.ok(category1s);
    }

    @GetMapping("/getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable("category1Id") Long category1Id){
        //按照规范，controller不能写业务逻辑，应该写在service层，所以这里应该调用service层的方法
        //QueryWrapper<BaseCategory2> queryWrapper = new QueryWrapper<>();
        //queryWrapper.eq("category1_id", category1Id);
        //List<BaseCategory2> category2s = baseCategory2Service.list(queryWrapper);

        List<BaseCategory2> category2s = baseCategory2Service.getCategory2sByCategory1Id(category1Id);

        return Result.ok(category2s);
    }

    @GetMapping("/getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable("category2Id") Long category2Id){
        //按照规范，controller不能写业务逻辑，应该写在service层，所以这里应该调用service层的方法
        //QueryWrapper<BaseCategory2> queryWrapper = new QueryWrapper<>();
        //queryWrapper.eq("category1_id", category1Id);
        //List<BaseCategory2> category2s = baseCategory2Service.list(queryWrapper);

        List<BaseCategory3> category3s = baseCategory3Service.getCategory3sByCategory2Id(category2Id);

        return Result.ok(category3s);
    }
}
