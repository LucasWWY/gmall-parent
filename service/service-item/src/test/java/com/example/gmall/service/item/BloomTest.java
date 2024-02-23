package com.example.gmall.service.item;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 22/2/2024 - 12:13 am
 * @Description
 */
public class BloomTest {

    @Test
    void bloomTest(){
        /**
         * Funnel<? super T> funnel, 布隆过滤器要存哪些数据
         * int expectedInsertions,  期望插入数量
         * double fpp 误判率
         */
        Funnel<CharSequence> funnel = Funnels.stringFunnel(StandardCharsets.UTF_8);
        //1、创建bf
        BloomFilter<CharSequence> filter = BloomFilter
                .create(funnel, 1000000, 0.000001);

        //2、给bf添一些元素
        filter.put("http://www.baidu.com");
        filter.put("http://www.qq.com");
        filter.put("http://www.jd.com");

        //3、判定哪个网页是否操作过
        String url = "http://www.jd.com";
        //判定可能包含； 判定是否存在
        System.out.println(filter.mightContain(url)); //有不一定有，没有一定没有
    }
}

