package com.example.gmall.service.product.service;

import com.example.gmall.service.product.entity.BaseAttrValue;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author wangweiyedemacbook
* @description 针对表【base_attr_value(属性值表)】的数据库操作Service
* @createDate 2024-02-06 23:58:52
*/
public interface BaseAttrValueService extends IService<BaseAttrValue> {

    List<BaseAttrValue> getAttrValueList(Long attrId);

}
