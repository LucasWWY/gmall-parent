package com.example.gmall.model.search;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 15/4/2024 - 6:43 am
 * @Description
 */
@Data
public class SearchAttr {

    // 平台属性Id
    @Field(type = FieldType.Long)
    private Long attrId;

    // 平台属性值名称
    @Field(type = FieldType.Keyword)
    private String attrValue;

    // 平台属性名
    @Field(type = FieldType.Keyword)
    private String attrName;

}
