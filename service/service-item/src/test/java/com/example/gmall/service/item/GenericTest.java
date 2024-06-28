package com.example.gmall.service.item;

import com.alibaba.fastjson.JSON;
import com.example.gmall.model.product.entity.SkuImage;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 12/3/2024 - 2:28 pm
 * @Description
 */

public class GenericTest {

    @Test
    void test2(){
        List<SkuImage> images = new ArrayList<>();
        SkuImage image = new SkuImage();
        image.setId(0L);
        image.setSkuId(0L);
        image.setImgName("0");
        image.setImgUrl("0");
        image.setSpuImgId(0L);
        image.setIsDefault("0");

        images.add(image);

        SkuImage image2 = new SkuImage();
        image2.setId(1L);
        image2.setSkuId(1L);
        image2.setImgName("1");
        image2.setImgUrl("1");
        image2.setSpuImgId(0L);
        image2.setIsDefault("1");

        images.add(image2);

        String json = JSON.toJSONString(images);
        System.out.println(json);

        Type type = null;
        for (Method method : GenericTest.class.getDeclaredMethods()) {
            if (method.getName().equals("getHaha")) { //List<SkuImage> getHaha()
                type = method.getGenericReturnType();
            }
        }

        Object o = JSON.parseObject(json, type);
        System.out.println("逆转的数据："+o);
    }

    @Test
    void test1(){
        System.out.println("test1");
        for (Method method : GenericTest.class.getDeclaredMethods()) {
            System.out.println(method.getName()+"===>"+method.getReturnType()); //interface java.util.List 不够精确
            System.out.println(method.getName()+"===>"+method.getGenericReturnType()); //java.util.List<com.example.gmall.service.product.entity.SkuImage>带范型
            //System.out.println(method.getName()+"===>"+method.getGenericReturnType()+"==>"+method.getDeclaringClass());
        }
    }

    List<SkuImage> getHaha(){
        return null;
    }

    String getHehe(){
        return "";
    }

    Map<String,List<SkuImage>> getHeihei(){
        return null;
    }

}
