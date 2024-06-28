package com.example.gmall.service.cart.rpc;

import com.example.gmall.service.cart.service.CartService;
import com.example.gmall.model.cart.entity.CartItem;
import com.example.gmall.model.cart.vo.AddCartSuccessVO;
import com.example.gmall.common.result.Result;
import com.example.gmall.model.product.entity.SkuInfo;
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        String cartKey = cartService.determineCartKey();

        SkuInfo skuInfo = cartService.addToCart(skuId, skuNum, cartKey);

        return Result.ok();
    }

    @DeleteMapping("/deleteChecked")
    public String deleteChecked(){

        String cartKey = cartService.determineCartKey();

        cartService.deleteChecked(cartKey);

        return "redirect:/cart.html"; //不然url还是deleteChecked，如果此时有选中的item，按下F5就会被删除
    }

    /**
     * 获取所有选中的商品，方便结算
     * @return
     */
    @GetMapping("/checkeds")
    public Result<List<CartItem>> getChecked(){

        String cartKey = cartService.determineCartKey();

        List<CartItem> checkeds = cartService.getCheckeds(cartKey);

        return Result.ok(checkeds);
    }

}
