package com.example.gmall.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 15/4/2024 - 2:37 am
 * @Description
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(indexName = "person")  //ES object mapping
public class Person {

    @Id
    private Long id;

    @Field(value = "name",type = FieldType.Text) //文本字段能全文检索
    private String name;

    @Field(value = "age",type = FieldType.Integer)
    private Integer age;
}
