package com.example.gmall.service.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.gmall.service.product.entity.BaseCategory2;
import com.example.gmall.service.product.mapper.BaseCategory2Mapper;
import com.example.gmall.service.product.service.BaseCategory2Service;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author wangweiyedemacbook
* @description 针对表【base_category2(二级分类表)】的数据库操作Service实现
* @createDate 2024-02-06 23:58:52
*/
@Service
public class BaseCategory2ServiceImpl extends ServiceImpl<BaseCategory2Mapper, BaseCategory2>
    implements BaseCategory2Service{



    @Override
    public List<BaseCategory2> getCategory2sByCategory1Id(Long category1Id) {
        QueryWrapper<BaseCategory2> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category1_id", category1Id);
        List<BaseCategory2> category2s = list(queryWrapper); // list()来自于ServiceImpl
        return category2s;
    }
}




