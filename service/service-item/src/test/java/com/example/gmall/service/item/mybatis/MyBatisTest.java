package com.example.gmall.service.item.mybatis;

import com.example.gmall.service.item.mybatis.dao.PersonDao;
import com.example.gmall.service.item.mybatis.sql.MySqlSession;

/**
 * @author lfy
 * @Description
 * @create 2022-12-09 9:34
 */
public class MyBatisTest {

    public static void main(String[] args) {
        MySqlSession sqlSession = new MySqlSession();

        //1、得到代理对象
        PersonDao mapper = sqlSession.getMapper(PersonDao.class);
        //System.out.println(mapper);

        mapper.getAllPersonCount();
        System.out.println("=======");
        mapper.insertPerson(8,"aaa@qq.com");
    }
}
