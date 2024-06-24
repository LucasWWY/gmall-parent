package com.example.gmall.feign.cart;

import com.example.gmall.cart.entity.CartItem;
import com.example.gmall.cart.vo.AddCartSuccessVO;
import com.example.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 20/6/2024 - 10:52 pm
 * @Description
 */
@RequestMapping("/api/inner/rpc/cart") // 虽然服务名称是唯一的，但具体服务内可能有多个不同的功能和端点。使用@RequestMapping可以明确指定每个功能的URL路径。
@FeignClient("service-cart")
public interface CartFeignClient {

    @GetMapping("/add/{skuId}") // /api/inner/rpc/cart + /add/{skuId} 在服务实例内将请求路由到具体的处理方法
    Result<AddCartSuccessVO> addToCart(@PathVariable("skuId") Long skuId,
                                       @RequestParam Integer skuNum);

    /**
     * 删除选中的
     * @return
     */
    @DeleteMapping("/deleteChecked")
    Result deleteChecked();


    /**
     * 获取所有选中的商品
     * @return
     */
    @GetMapping("/checkeds")
    Result<List<CartItem>> getChecked();


}
