package com.example.gmall.service.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.gmall.service.product.entity.BaseCategory2;

import java.util.List;

/**
* @author wangweiyedemacbook
* @description 针对表【base_category2(二级分类表)】的数据库操作Service
* @createDate 2024-02-06 23:58:52
*/
public interface BaseCategory2Service extends IService<BaseCategory2> {

    List<BaseCategory2> getCategory2sByCategory1Id(Long category1Id);
}
