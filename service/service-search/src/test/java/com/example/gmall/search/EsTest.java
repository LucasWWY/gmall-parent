package com.example.gmall.search;

import com.example.gmall.bean.Person;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 9/4/2024 - 5:14 pm
 * @Description
 */
@SpringBootTest
public class EsTest {

    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Test
    void testSearch(){
        //1. 利用boolQuery组合多条件
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery(); //QueryBuilders 带s一般是工具类

        boolQuery.must(QueryBuilders.rangeQuery("age").gte(20L));
        boolQuery.must(QueryBuilders.matchQuery("name","张"));

        //2. 原生检索: 代表能使用原生的DSL进行检索
        Query query = new NativeSearchQuery(boolQuery);

        //3. 检索命中的记录信息
        SearchHits<Person> hits = elasticsearchRestTemplate.search(query, Person.class, IndexCoordinates.of("person"));


        System.out.println("检索结果："+hits);

        for (SearchHit<Person> hit : hits.getSearchHits()) {
            Person person = hit.getContent();
            System.out.println(person);
        }
    }

    //Spring data elasticsearch提供了简化的crud操作

}
