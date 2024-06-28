package com.example.gmall.service.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.gmall.model.product.entity.BaseCategory1;
import com.example.gmall.model.product.entity.BaseCategory2;
import com.example.gmall.model.product.entity.BaseCategory3;
import com.example.gmall.model.product.vo.CategoryTreeVO;

import java.util.List;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 6/2/2024 - 7:18 pm
 * @Description
 */
public interface BaseCategory1Service extends IService<BaseCategory1> {
    List<CategoryTreeVO> getCategoryTree();

    List<BaseCategory2> getCategory2sByCategory1Id(Long category1Id);

    List<BaseCategory3> getCategory3sByCategory2Id(Long category2Id);

    CategoryTreeVO getCategoryTreeWithC3Id(Long c3Id);
}
