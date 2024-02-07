package com.example.gmall.service.product.service;

import com.example.gmall.service.product.entity.BaseCategory3;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author wangweiyedemacbook
* @description 针对表【base_category3(三级分类表)】的数据库操作Service
* @createDate 2024-02-06 23:58:52
*/
public interface BaseCategory3Service extends IService<BaseCategory3> {

    List<BaseCategory3> getCategory3sByCategory2Id(Long category2Id);
}
