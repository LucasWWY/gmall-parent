package com.example.gmall.search.repo;

import com.example.gmall.search.bean.Person;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 15/4/2024 - 2:51 am
 * @Description
 */
@Repository
//public interface PersonRepository extends CrudRepository<Person, Long> { //CrudRepository有一个子接口PagingAndSortingRepository
public interface PersonRepository extends PagingAndSortingRepository<Person, Long> {

    List<Person> findAllByAgeGreaterThanEqual(Integer age);

    void deleteByAgeLessThan(Integer age);

}
