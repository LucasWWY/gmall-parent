package com.example.gmall.controller;

import com.example.gmall.cart.entity.CartItem;
import com.example.gmall.common.result.Result;
import com.example.gmall.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author lfy
 * @Description
 * 1、有有效时间吗？是多久？
 * 2、商品数量的限制；
 *     限制场景： 库存、优惠、普通
 *     普通：
 *       1）、任何商品单个数量不能超过200
 *       2）、购物车商品总种类不能超过200
 * 3、价格同步
 *   1）、购物车确实缓存最新价格，进行同步；
 *          懒思想：查询购物车列表的时候，再次检索一下最新价格，如果有变化就同步即可
 *   2）、购物车展示列表的时候，在查询一下价格；
 *
 * 4、其他同步：
 *    1、上下架状态同步：
 *    2、优惠信息变更同步：
 *    3、价格同步：
 *    同步xx数据的两种方式：
 *      1）、懒思想：用的时候后台再慢慢查就行； 节流
 *      2）、前端直接用的时候发请求要； 此时需要做节流；
 *
 *
 *
 * @create 2022-12-20 8:57
 */
@RequestMapping("/api/cart")
@RestController
public class CartApiController {


    @Autowired
    CartService cartService;

    /**
     * 获取购物车商品列表
     * @return
     */
    @GetMapping("/cartList")
    public Result getCartList(){
        //List<CartItem> cartList = cartService.displayItems(cartKey);
        List<CartItem> cartList = cartService.displayItems();
        return Result.ok(cartList);
    }


    /**
     * 修改购物车商品数量
     *
     * 1、需要前端区分请求；
     * @param skuId
     * @param num   1或-1  是一个增量
     * @return
     */
    @PostMapping("/addToCart/{skuId}/{num}")
    public Result updateItemNum(@PathVariable("skuId") Long skuId,
                                @PathVariable("num") Integer num){

        //为什么非要再controller这里获取cartKey,不在serviceImpl的方法中直接调用
        //更灵活，临时购物车 和 用户购物车 可以通过传递不同cartKey选择操作哪一个
        String cartKey = cartService.determineCartKey();

        cartService.updateItemNum(cartKey,skuId,num);

        return Result.ok();
    }


    /**
     * 选中、不选中
     * @param skuId
     * @param checked
     * @return
     */
    @GetMapping("/checkCart/{skuId}/{checked}")
    public Result checkItem(@PathVariable("skuId") Long skuId,
                            @PathVariable("checked") Integer checked){

        String cartKey = cartService.determineCartKey();
        cartService.checkItem(cartKey, skuId, checked);

        return Result.ok();
    }


    /**
     * 删除购物车中某一项 注意和删除选中商品进行区分
     * @param skuId
     * @return
     */
    @DeleteMapping("/deleteCart/{skuId}")
    public Result deleteItem(@PathVariable("skuId") Long skuId){

        String cartKey = cartService.determineCartKey();

        cartService.deleteItem(cartKey,skuId);

        return Result.ok();
    }
}

