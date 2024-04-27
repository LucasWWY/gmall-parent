package com.example.gmall.service.product.service;

import com.example.gmall.search.SearchAttr;
import com.example.gmall.service.product.entity.SkuAttrValue;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author wangweiyedemacbook
* @description 针对表【sku_attr_value(sku平台属性值关联表)】的数据库操作Service
* @createDate 2024-02-06 23:58:53
*/
public interface SkuAttrValueService extends IService<SkuAttrValue> {

    List<SearchAttr> getSkuAttrsAndValue(Long skuId);
}
