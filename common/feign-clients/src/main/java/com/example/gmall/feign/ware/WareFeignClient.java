package com.example.gmall.feign.ware;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 27/6/2024 - 3:15 pm
 * @Description
 */
@FeignClient(value = "ware-manager", url = "http://locaLhost:9001")
//如果在nacos注册了，那么可以直接写服务名@FeignClient("ware-manager") 自动去nacos得到服务的ip地址
//没注册就需要手动声明微服务的IP地址
public interface WareFeignClient {


    //http://locaLhost:9001/hasstock?skuId=43&num=2
    @GetMapping("/hasStock")
    String hasStock(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num); //num是为了检测足够不足够需要的数量
}
