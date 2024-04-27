package com.example.gmall.service.product.mapper;

import com.example.gmall.search.SearchAttr;
import com.example.gmall.service.product.entity.SkuAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author wangweiyedemacbook
* @description 针对表【sku_attr_value(sku平台属性值关联表)】的数据库操作Mapper
* @createDate 2024-02-06 23:58:53
* @Entity com.example.gmall.service.product.entity.SkuAttrValue
*/
public interface SkuAttrValueMapper extends BaseMapper<SkuAttrValue> {
    List<SearchAttr> getSkuAttrsAndValue(@Param("skuId") Long skuId);
}




