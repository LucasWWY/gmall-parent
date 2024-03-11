package com.example.gmall.service.item.mybatis.dao;

import com.example.gmall.service.item.mybatis.annotation.MySQL;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 9/3/2024 - 8:08 pm
 * @Description
 */
public interface PersonDao {

    @MySQL("select count(*) from person")
    Integer getAllPersonCount();

    @MySQL("insert into person('age','email') values({age},{email})")
    void insertPerson(Integer age, String email);
}

