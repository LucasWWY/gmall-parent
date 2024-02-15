package com.example.gmall.service.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.gmall.service.product.entity.BaseCategory1;
import com.example.gmall.service.product.entity.BaseCategory2;
import com.example.gmall.service.product.entity.BaseCategory3;
import com.example.gmall.service.product.mapper.BaseCategory1Mapper;
import com.example.gmall.service.product.service.BaseCategory1Service;
import com.example.gmall.service.product.service.BaseCategory2Service;
import com.example.gmall.service.product.service.BaseCategory3Service;
import com.example.gmall.service.product.vo.CategoryTreeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 6/2/2024 - 7:19 pm
 * @Description
 */
@Service
public class BaseCategory1ServiceImpl extends ServiceImpl<BaseCategory1Mapper, BaseCategory1> implements BaseCategory1Service {

    @Autowired
    BaseCategory1Mapper baseCategory1Mapper;

    @Autowired
    BaseCategory2Service baseCategory2Service;

    @Autowired
    BaseCategory3Service baseCategory3Service;


    @Override
    public List<CategoryTreeVO> getCategoryTree() {
        //select bc1.*, bc2.*, bc3.* from base_category1 bc1
        //left join base_category2 bc2 on bc1.id = bc2.category1_id
        //left join base_category3 bc3 on bc2.id = bc3.category2_id

        List<CategoryTreeVO> categoryTreeVOs = baseCategory1Mapper.getCategoryTree();
        //ServiceImpl类已经通过泛型指定了Mapper接口和对应的实体类，MyBatis Plus会自动将Mapper注入到Service中。????
        return categoryTreeVOs;
    }

    @Override
    public List<BaseCategory2> getCategory2sByCategory1Id(Long category1Id) {
        QueryWrapper<BaseCategory2> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category1_id", category1Id);
        List<BaseCategory2> category2s = baseCategory2Service.list(queryWrapper); // list()来自于ServiceImpl
        return category2s;
    }

    @Override
    public List<BaseCategory3> getCategory3sByCategory2Id(Long category2Id) {
        QueryWrapper<BaseCategory3> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category2_id", category2Id);
        List<BaseCategory3> category3s = baseCategory3Service.list(queryWrapper);
        return category3s;
    }

    @Override
    public CategoryTreeVO getCategoryTreeWithC3Id(Long c3Id) {
        CategoryTreeVO categoryTreeVO = baseCategory1Mapper.getCategoryTreeWithC3Id(c3Id);
        return categoryTreeVO;
    }
}
