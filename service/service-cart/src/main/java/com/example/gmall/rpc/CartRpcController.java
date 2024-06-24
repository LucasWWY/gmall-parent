package com.example.gmall.rpc;

import com.example.gmall.cart.vo.AddCartSuccessVO;
import com.example.gmall.common.result.Result;
import com.example.gmall.service.CartService;
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 20/6/2024 - 10:47 pm
 * @Description
 */
@Slf4j
@RequestMapping("/api/inner/rpc/cart")
@RestController
public class CartRpcController {

    @Autowired
    CartService cartService;

    /**
     * 添加商品到购物车
     * @param skuId
     * @param skuNum
     * @return
     */
    @GetMapping("/add/{skuId}")
    public Result<AddCartSuccessVO> addToCart(@PathVariable("skuId") Long skuId,
                                              @RequestParam Integer skuNum
                                              //@RequestHeader(value = RedisConst.USER_ID_HEADER, required = false) userId,
                                              //@RequestHeader(value = RedisConst.USER_TEMP_ID_HEADER, required = false) userTempId,
                                              //跨层传递数据(方法参数，线程绑定，共享到公共位置(e.g.redis, mysql))
                                              //SpringMVC自带的RequestContextHolder实现了线程绑定
                                              ) {
        AddCartSuccessVO addCartSuccessVO = cartService.addToCart(skuId, skuNum);


        return Result.ok();
    }

}
