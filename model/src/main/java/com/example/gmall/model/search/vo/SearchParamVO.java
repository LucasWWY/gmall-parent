package com.example.gmall.model.search.vo;

import lombok.Data;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 15/4/2024 - 5:14 am
 * @Description 封装检索用的所有参数
 */
@Data
public class SearchParamVO {
    Long category1Id;
    Long category2Id;
    Long category3Id;
    //以上是分类相关参数  category3Id=61

    // /list.htm1?category3Id=61&trademark=2:华为&props=4:256GB:机身存储&props=3:8GB:运行内存&order:2:desc&pageNo=1&keyword=华为
    //有时候可能有category3Id，有时候可能没有，所以不能简单使用Spring Data ES自动生成的方法，需要自己用ESRestTemplate写DSL语句

    //关键字检索
    String keyword;  //keyword=华为

    //品牌检索
    String trademark; //trademark=2:华为

    //平台属性
    String[] props; //props=4:256GB:机身存储&props=3:8GB:运行内存


    //排序方式 1：综合(热度分) 2：价格
    //desc or asc
    String order = "1:desc"; //order=2:desc

    //页码
    Integer pageNo = 1; //pageNo=1
}
