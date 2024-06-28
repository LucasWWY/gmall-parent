package com.example.gmall.service.product.controller;

import com.example.gmall.common.result.Result;
import com.example.gmall.model.product.entity.BaseAttrInfo;
import com.example.gmall.model.product.entity.BaseAttrValue;
import com.example.gmall.service.product.service.BaseAttrInfoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 7/2/2024 - 11:48 pm
 * @Description
 */
@RequestMapping("/admin/product")
@RestController
public class BaseAttrController {

    @Autowired
    BaseAttrInfoService baseAttrInfoService;

    @ApiOperation("根据分类id获取平台属性")
    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result getBaseAttr(@PathVariable("category1Id") Long category1Id,
                              @PathVariable("category2Id") Long category2Id,
                              @PathVariable("category3Id") Long category3Id) {
        List<BaseAttrInfo> attrInfos = baseAttrInfoService.getAttrInfoAndValue(category1Id, category2Id, category3Id);
        return Result.ok(attrInfos);
    }

    @PostMapping("saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo) {
        if (baseAttrInfo.getId() != null) {
            baseAttrInfoService.updateAttrInfo(baseAttrInfo);
        } else {
            baseAttrInfoService.saveAttrInfo(baseAttrInfo);
        }

        return Result.ok();
    }

    @GetMapping("getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable("attrId") Long attrId) {
        List<BaseAttrValue> attrValues = baseAttrInfoService.getAttrValueList(attrId);
        return Result.ok(attrValues);
    }
}
