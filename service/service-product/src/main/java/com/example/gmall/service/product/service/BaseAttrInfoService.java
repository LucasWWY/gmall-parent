package com.example.gmall.service.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.gmall.service.product.entity.BaseAttrInfo;
import com.example.gmall.service.product.entity.BaseAttrValue;

import java.util.List;

/**
* @author wangweiyedemacbook
* @description 针对表【base_attr_info(属性表)】的数据库操作Service
* @createDate 2024-02-06 23:58:52
*/
public interface BaseAttrInfoService extends IService<BaseAttrInfo> {

    List<BaseAttrInfo> getAttrInfoAndValue(Long category1Id, Long category2Id, Long category3Id);

    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    void updateAttrInfo(BaseAttrInfo baseAttrInfo);

    List<BaseAttrValue> getAttrValueList(Long attrId);
}
