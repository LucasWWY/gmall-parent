package com.example.gmall.service.product.mapper;

import com.example.gmall.service.product.entity.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author wangweiyedemacbook
* @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Mapper
* @createDate 2024-02-06 23:58:53
* @Entity com.example.gmall.service.product.entity.SpuSaleAttr
*/
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {

    //@Param("spuId")用于指定参数的名称为"spuId"，这样在XML映射文件中可以通过#{spuId}来引用这个参数
    List<SpuSaleAttr> getSpuSaleAttrList(@Param("spuId") Long spuId);
}




