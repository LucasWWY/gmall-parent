package com.example.search.search;

import com.example.gmall.search.service.SearchService;
import com.example.gmall.search.vo.SearchParamVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 20/4/2024 - 9:10 pm
 * @Description
 */
@SpringBootTest
public class SearchTest {

    @Autowired
    SearchService searchService;

    @Test
    void testSearch() {
        SearchParamVO searchParamVO = new SearchParamVO();
        searchParamVO.setCategory3Id(61L);
        searchParamVO.setTrademark("2:华为");
        searchParamVO.setProps(new String[]{"4:256GB:机身存储", "5:8GB:运行内存"});
        searchParamVO.setOrder("2:desc");
        searchParamVO.setPageNo(1);
        searchParamVO.setKeyword("华为");

        searchService.search(searchParamVO); //在yml中设置logging:level:tracer
    }

}
