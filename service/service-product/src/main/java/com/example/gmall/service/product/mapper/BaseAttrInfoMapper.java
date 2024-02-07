package com.example.gmall.service.product.mapper;

import com.example.gmall.service.product.entity.BaseAttrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author wangweiyedemacbook
* @description 针对表【base_attr_info(属性表)】的数据库操作Mapper
* @createDate 2024-02-06 23:58:52
* @Entity com.example.gmall.service.product.entity.BaseAttrInfo
*/
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {

    List<BaseAttrInfo> getAttrInfoAndValue(@Param("category1Id") Long category1Id,
                                           @Param("category2Id") Long category2Id,
                                           @Param("category3Id") Long category3Id);
}




