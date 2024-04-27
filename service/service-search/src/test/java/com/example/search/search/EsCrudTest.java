package com.example.search.search;

import com.example.gmall.search.bean.Person;
import com.example.gmall.search.repo.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 15/4/2024 - 2:59 am
 * @Description
 */
@SpringBootTest
public class EsCrudTest {

    @Autowired
    PersonRepository personRepository;

    @Test
    void testCrud(){
        List<Person> people = Arrays.asList(
                new Person(1L, "张三", 18),
                new Person(2L, "张四", 19),
                new Person(3L, "李三", 20),
                new Person(4L, "李四", 21)
        );
        personRepository.saveAll(people);
        System.out.println("保存完成....");

        Iterable<Person> all = personRepository.findAll();
        all.forEach(item->{
            System.out.println(item);
        });
    }

    @Test
    void testQuery(){
        List<Person> all = personRepository.findAllByAgeGreaterThanEqual(19);
        all.forEach(item->{
            System.out.println(item);
        });
    }



}
