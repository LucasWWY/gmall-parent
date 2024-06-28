package com.example.gmall.service.product;

import com.example.gmall.model.product.entity.SkuImage;
import com.example.gmall.service.product.mapper.SkuImageMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 19/2/2024 - 5:22 pm
 * @Description
 */
@SpringBootTest
public class RWTest {

    @Autowired
    SkuImageMapper skuImageMapper;


    @Transactional //方法二：事务内的所有读写都要去主库。
    @Test
    void testTransaction(){
        //1、修改
        SkuImage image = new SkuImage();
        image.setId(270L);
        image.setSkuId(1L);
        image.setImgName("bbbb");
        image.setImgUrl("aaa~~");
        image.setSpuImgId(1L);
        image.setIsDefault("1");
        skuImageMapper.updateById(image);
        System.out.println("修改完成....");

        //2、读取; 上次修改的同一个数据，下次读不要去从库。有可能从还没同步上
        //强制从主库读取
        //HintManager.getInstance().setWriteRouteOnly(); //方法一：设置仅主库路由
        skuImageMapper.selectById(270L);
        skuImageMapper.selectById(270L); //mybatis的缓存机制，同一个事务期间，同一sql直接去缓存拿
        System.out.println(image);
    }


    @Test //读去从库，负载均衡（Round-robin 轮询）
    void  testread(){
        SkuImage image1 = skuImageMapper.selectById(270L);
        SkuImage image2 = skuImageMapper.selectById(270L);
        SkuImage image3 = skuImageMapper.selectById(270L);
        SkuImage image4 = skuImageMapper.selectById(270L);
    }

    @Test //测试读写分离: 写去主库
    void testrw(){
        SkuImage image = new SkuImage();
        image.setSkuId(1L);
        image.setImgName("aaa");
        image.setImgUrl("aaa");
        image.setSpuImgId(1L);
        image.setIsDefault("1");

        skuImageMapper.insert(image);
        System.out.println("插入完成...");
    }

}
