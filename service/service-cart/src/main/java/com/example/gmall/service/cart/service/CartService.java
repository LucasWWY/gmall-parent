package com.example.gmall.service.cart.service;

import com.example.gmall.model.cart.entity.CartItem;
import com.example.gmall.model.product.entity.SkuInfo;

import java.util.List;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 22/6/2024 - 9:20 pm
 * @Description
 */
public interface CartService {

    /**
     * 把商品添加到购物车
     * @param skuId    添加的商品
     * @param skuNum      添加的数量
     * @return
     */
    SkuInfo addToCart(Long skuId, Integer skuNum, String cartKey);

    /**
     * 决定要操作哪个购物车
     * @return
     */
    String determineCartKey();

    /**
     * 从购物车中获取一个商品
     * @param cartKey
     * @param skuId
     * @return
     */
    CartItem getItem(String cartKey, Long skuId);

    /**
     * 保存一项到购物车
     * @param cartKey
     * @param item
     */
    void saveItem(String cartKey, CartItem item);


    /**
     * 查询某个购物车的商品列表
     * @param caryKey
     * @return
     */
    List<CartItem> getCartItems(String caryKey);

    /**
     * 专供购物车列表用的
     * @return
     */
    List<CartItem> displayItems();

    /**
     * 修改购物车商品的数量
     * @param cartKey
     * @param skuId
     * @param num
     */
    void updateItemNum(String cartKey, Long skuId, Integer num);

    /**
     * 选中、不选中
     * @param cartKey
     * @param skuId
     * @param checked
     */
    void checkItem(String cartKey, Long skuId, Integer checked);

    /**
     * 删除购物车中某一项
     * @param cartKey
     * @param skuId
     */
    void deleteItem(String cartKey, Long skuId);

    /**
     * 删除选中的
     * @param cartKey
     */
    void deleteChecked(String cartKey);


    /**
     * 获取选中的商品
     * @param cartKey
     * @return
     */
    List<CartItem>  getCheckeds(String cartKey);
}
