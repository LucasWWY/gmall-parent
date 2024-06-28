package com.example.gmall.service.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.gmall.model.product.entity.BaseCategory1;
import com.example.gmall.model.product.vo.CategoryTreeVO;

import java.util.List;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 6/2/2024 - 7:17 pm
 * @Description
 */
//@Mapper // This annotation is not needed since we have added @MapperScan in ServiceProductApplication.java
public interface BaseCategory1Mapper extends BaseMapper<BaseCategory1> {
    List<CategoryTreeVO> getCategoryTree();

    CategoryTreeVO getCategoryTreeWithC3Id(Long c3Id);
}
