package com.example.gmall.service.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.gmall.common.result.Result;
import com.example.gmall.model.product.entity.BaseTrademark;
import com.example.gmall.service.product.service.BaseTrademarkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 11/2/2024 - 6:12 pm
 * @Description
 */
@Api(tags = "品牌管理")
@RestController
@RequestMapping("/admin/product/baseTrademark")
public class BaseTrademarkController {

    @Autowired
    BaseTrademarkService baseTrademarkService;

    @ApiOperation("查询所有品牌")
    @GetMapping("/getTrademarkList")
    public Result getTrademarkList() {
        List<BaseTrademark> list = baseTrademarkService.list();
        return Result.ok(list);
    }

    @ApiOperation("分页获取品 牌列表")
    @GetMapping("/{pn}/{ps}")
    public Result getTrademark(@PathVariable("pn") Long pn,
                               @PathVariable("ps") Long ps){
        Page<BaseTrademark> page = baseTrademarkService.page(new Page<BaseTrademark>(pn,ps));
        return Result.ok(page);
    }

    @ApiOperation("保存品牌")
    @PostMapping("/save")
    public Result save(@RequestBody BaseTrademark baseTrademark){

        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }

    @ApiOperation("删除品牌")
    @DeleteMapping("/remove/{id}")
    public Result delete(@PathVariable("id") Long id){
        baseTrademarkService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("查询品牌")
    @GetMapping("/get/{id}")
    public Result get(@PathVariable("id") Long id){
        BaseTrademark trademark = baseTrademarkService.getById(id);
        return Result.ok(trademark);
    }

    @ApiOperation("修改品牌")
    @PutMapping("/update")
    public Result update(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }

}
