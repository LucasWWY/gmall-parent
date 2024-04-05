package com.example.gmall.feign.product;


import com.example.gmall.common.result.Result;
import com.example.gmall.service.product.vo.CategoryTreeVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 15/2/2024 - 12:48 am
 * @Description
 * 3/4/2024: 抽取到common下的feign-clients模块
 */
@RequestMapping("/api/inner/rpc/product") //1. 说清楚调用哪个微服务的哪个controller
@FeignClient("service-product") // 为名为CategoryFeignClient的feign client所有配置集中在一个bean中，即service-product.FeignClientSpecification
public interface CategoryFeignClient {

    //给远程的 service-product 发送get请求，路径是ip:port/api/inner/rpc/product/category/tree?haha=ddd

    /**
     * 1、先去 nacos注册中心 找到 @FeignClient 注解说明的 service-product 对应的 ip + port
     * 2、按照声明的GET方式请求ip+port+路径（feign在这一步帮我们完成了序列化）
     * 3、对方处理完成以后，给feign返回json数据
     * 4、feign把接受到的json数据自动转为 方法指定的类型（feign在这一步帮我们完成了反序列化）
     *
     * controller：
     *      @xxxMapping： 接受各种方式的请求
     *      @RequestParam：接受请求参数中的值
     *      @RequestBody：接受请求体中的值
     *      @PathVariable: 接受请求路径的值
     *      @RequestHeader：接受请求头的值
     * feignclient:
     *      @xxxMapping： 发送各种方式的请求
     *      @RequestParam：方法形参的值放到请求参数中发送出去
     *      @RequestBody： 方法形参的值放到请求体中发送出去
     *      @PathVariable: 方法形参的值放到请求路径中发送出去
     *      @RequestHeader：方法形参的值放到请求头中发送出去
     *      e.g. public Result<List<CategoryTreeVO>> getCategoryTree(@RequestParam("haha") String haha)
     *      categoryFeignClient.getCategoryTree("haha"));
     *      相当于给远程的 service-product 发送get请求，路径是ip:port/api/inner/rpc/product/category/tree?haha=ddd
     * @return
     */
    @GetMapping("/category/tree") //2. 说清楚调用哪个接口
    Result<List<CategoryTreeVO>> getCategoryTree(); //3. 说清楚调用的接口的返回值类型
}
