package com.example.gmall.service.product.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 14/2/2024 - 11:24 pm
 * @Description
 */
@Data
public class CategoryTreeVO {
    private Long categoryId; //分类id

    private String categoryName; //分类名字

    private List<CategoryTreeVO> categoryChild;//子分类, 无限嵌套
}
