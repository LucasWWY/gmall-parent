package com.example.gmall.service;

import com.example.gmall.cart.entity.CartInfo;
import com.example.gmall.service.product.entity.SkuInfo;

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
    SkuInfo addToCart(Long skuId, Integer skuNum);

    /**
     * 决定要操作哪个购物车
     * @return
     */
    String determinCartKey();

    /**
     * 从购物车中获取一个商品
     * @param cartKey
     * @param skuId
     * @return
     */
    CartInfo getItem(String cartKey, Long skuId);

    /**
     * 保存一项到购物车
     * @param cartKey
     * @param item
     */
    void saveItem(String cartKey, CartInfo item);


    /**
     * 查询某个购物车的商品列表
     * @param caryKey
     * @return
     */
    List<CartInfo> getCartItems(String caryKey);


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
     * 专供购物车列表用的
     * @return
     */
    List<CartInfo> displayItems();


    /**
     * 获取选中的商品
     * @param cartKey
     * @return
     */
    List<CartInfo>  getCheckeds(String cartKey);
}
