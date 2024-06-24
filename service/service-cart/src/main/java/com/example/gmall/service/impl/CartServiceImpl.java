package com.example.gmall.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.gmall.cart.entity.CartInfo;
import com.example.gmall.common.authen.UserAuthUtils;
import com.example.gmall.common.constant.RedisConst;
import com.example.gmall.common.execption.GmallException;
import com.example.gmall.common.result.ResultCodeEnum;
import com.example.gmall.feign.product.ProductSkuDetailFeignClient;
import com.example.gmall.service.CartService;
import com.example.gmall.service.product.entity.SkuInfo;
import com.example.gmall.user.vo.UserAuthInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 22/6/2024 - 9:20 pm
 * @Description
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    StringRedisTemplate redisTemplate; //专门用于处理字符串类型的键和值


    @Autowired
    ProductSkuDetailFeignClient skuDetailFeignClient;

    @Autowired
    ThreadPoolExecutor executor;


    //添加到购物车
    @Override
    public SkuInfo addToCart(Long skuId, Integer skuNum) {
        //1. 获取当前用户信息 工具类方法调用也是同一个线程
        //UserAuthInfoVO info = UserAuthUtils.getUserAuthInfo();
        //Long userId = info.getUserId();
        //Long userTempId = info.getUserTempId();

        //2. 得到redis中购物车用的key：cart:info:拼userId还是userTempId取决于登没登录
        String cartKey = determinCartKey(); //前两步都封装到这里面去
        //cart:info:3 hash结构 (49: skuInfo)

        //从redis拿到商品：以前购物车是否有这个商品
        CartInfo item = getItem(cartKey, skuId);
        //1、没有：添加
        if (item == null) {
            CartInfo itemToSave = prepareCartInfo(skuId, skuNum); //远程调用service-product得到商品信息
            //保存到redis
            saveItem(cartKey, itemToSave);
            //给前端准备返回用的数据
            SkuInfo skuInfo = convertItemToSkuInfo(itemToSave);
            return skuInfo;
        } else {
            //2、有：修改数量
            item.setSkuNum(item.getSkuNum() + skuNum); //skuNum是addToCart()传递的，所以只给增减量
            item.setUpdateTime(new Date());
            BigDecimal price = skuDetailFeignClient.getPrice(skuId).getData(); //获得最新价格
            item.setSkuPrice(price);
            //保存到redis
            saveItem(cartKey, item);
            SkuInfo skuInfo = convertItemToSkuInfo(item);
            return skuInfo;
        }
    }

    @Override
    public String determinCartKey() {

        //1. 获取当前用户信息 工具类方法调用也是同一个线程
        UserAuthInfoVO info = UserAuthUtils.getUserAuthInfo();
        Long userId = info.getUserId();
        Long userTempId = info.getUserTempId();

        //2. 得到redis中购物车用的key：cart:info:拼userId还是userTempId取决于登没登录
        String cartKey = RedisConst.CART_INFO;
        if (userId == null) {
            cartKey = cartKey + userTempId;
        } else {
            cartKey = cartKey + userId;
        }
        return cartKey;
    }

    @Override
    public CartInfo getItem(String cartKey, Long skuId) {
        //1、拿到商品
        Object item = redisTemplate.opsForHash()
                .get(cartKey, skuId.toString()); //hash结构 hash的key是skuId
        if (item == null) {
            return null;
        }

        String json = item.toString(); //redis本身存储的是序列化的字节数据
        //2、如果有，就逆转
        CartInfo info = JSON.parseObject(json, CartInfo.class);
        return info;
    }

    private CartInfo prepareCartInfo(Long skuId, Integer num) {
        SkuInfo skuInfo = skuDetailFeignClient.getSkuInfo(skuId).getData();
        CartInfo itemToSave = new CartInfo();
        itemToSave.setSkuId(skuInfo.getId());
        itemToSave.setCartPrice(skuInfo.getPrice());
        itemToSave.setSkuPrice(skuInfo.getPrice());
        itemToSave.setSkuNum(num);
        itemToSave.setImgUrl(skuInfo.getSkuDefaultImg());
        itemToSave.setSkuName(skuInfo.getSkuName());
        itemToSave.setIsChecked(1);
        itemToSave.setCreateTime(new Date());
        itemToSave.setUpdateTime(new Date());
        return itemToSave;
    }

    @Override
    public void saveItem(String cartKey, CartInfo item) {

        //1、购物车单个商品不超200
        if (item.getSkuNum() >= RedisConst.CART_ITEM_NUM_LIMIT) {
            throw new GmallException(ResultCodeEnum.CART_ITEM_NUM_OVERFLOW);
        }
        //2、一个购物车不同商品的种类不超过200
        Long size = redisTemplate.opsForHash().size(cartKey); //199
        //BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartKey); //=操作绑定了cartKey的hashOps，也就可以看做得到了一个购物车

        Boolean hasKey = redisTemplate.opsForHash()
                .hasKey(cartKey, item.getSkuId().toString()); //这个商品是否已经存在
        if (!hasKey) {
            if (size + 1 >= 200) { //考虑加入种类的item进去会不会超过使得总种类200
                throw new GmallException(ResultCodeEnum.CART_ITEM_COUNT_OVERFLOW);
            }
        }

        redisTemplate.opsForHash()
                .put(cartKey, item.getSkuId().toString(), JSON.toJSONString(item));

    }

    private SkuInfo convertItemToSkuInfo(CartInfo itemToSave) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setSkuName(itemToSave.getSkuName());
        skuInfo.setSkuDefaultImg(itemToSave.getImgUrl());
        skuInfo.setId(itemToSave.getSkuId());
        return skuInfo;
    }





    @Override
    public List<CartInfo> getCartItems(String cartKey) {

        List<CartInfo> infos = redisTemplate.opsForHash()
                .values(cartKey)
                .stream()
                .map(item -> JSON.parseObject(item.toString(), CartInfo.class))
                .sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())) //
                .collect(Collectors.toList());

        //同步最新价格
        //TODO 这里会有Bug，如果点击删除选中的商品，会先调用getCartItems,这样就会触发后台同步价格，
        // 结果导致删除的东西重新保存到redis，删不掉的情况； putIfPresent
        //解决：使用 putIfPresent操作即可
        CompletableFuture.runAsync(()->{
            syncPrice(cartKey, infos);
        },executor);

        return infos;
    }

    private void syncPrice(String cartKey, List<CartInfo> infos) {
        //节流代码
//        Long increment = redisTemplate.opsForValue().increment("price:"+cartKey);
//        if(increment%10 == 0){
//
//        }

        infos.stream() //并发
                .forEach(item->{
                    //远程查询价格
                    BigDecimal realPrice = skuDetailFeignClient.getPrice(item.getSkuId()).getData();
                    if (Math.abs(item.getSkuPrice().doubleValue() - realPrice.doubleValue()) >= 0.0001) {
                        //说明价格发生了变化
                        log.info("购物车：{}，中的商品{}，价格发生变化，最新为：{}", cartKey,item.getSkuId(),realPrice);
                        item.setSkuPrice(realPrice);
                        saveItem(cartKey,item);
                    }
                });


    }

    public static void aaaTest(String[] args) {
        System.out.println("a");
        Arrays.asList(1,2,3,4,5,6)
                .stream()
                .parallel()
                .forEach(item->{
                    System.out.println(Thread.currentThread()+">："+item);
                });
        System.out.println("b");
    }

    @Override
    public void updateItemNum(String cartKey, Long skuId, Integer num) {
        //1、获取购物车商品
        CartInfo cartInfo = getItem(cartKey, skuId);
        if (num == 1 || num == -1) {
            cartInfo.setSkuNum(cartInfo.getSkuNum() + num);
        } else {
            cartInfo.setSkuNum(num);
        }
        //2、保存到redis
        saveItem(cartKey, cartInfo);

    }

    @Override
    public void checkItem(String cartKey, Long skuId, Integer checked) {
        if (!(checked == 1 || checked == 0)) {
            throw new GmallException(ResultCodeEnum.INVAILD_PARAM);
        }

        CartInfo item = getItem(cartKey, skuId);
        item.setIsChecked(checked);


        saveItem(cartKey, item);


    }

    @Override
    public void deleteItem(String cartKey, Long skuId) {
        redisTemplate.opsForHash().delete(cartKey, skuId.toString());
    }

    @Override
    public void deleteChecked(String cartKey) {
        //1、获取选中的商品
        List<CartInfo> checkeds = getCheckeds(cartKey);
        List<String> collect = checkeds.stream()
                .map(item -> item.getSkuId().toString())
                .collect(Collectors.toList());
        //2、如果删除很快运行完成，而同步价格后台线程慢慢运行，就会导致删不掉
        redisTemplate.opsForHash().delete(cartKey, collect.toArray());
    }

    @Override
    public List<CartInfo> getCheckeds(String cartKey) {
        //直接调用这个会有bug
//        List<CartInfo> cartItems = getCartItems(cartKey);
        List<CartInfo> collect = redisTemplate.opsForHash()
                .values(cartKey)
                .stream()
                .map(item -> JSON.parseObject(item.toString(), CartInfo.class))
                .sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()))
                .filter((o1) -> o1.getIsChecked() == 1)//
                .collect(Collectors.toList());
        return collect;
    }

    /**
     * 展示购物车中所有商品
     *
     * @return
     */
    @Override
    public List<CartInfo> displayItems() {
        //1、先判断用户是否登录了，且临时购物车有数据
        //1）、得到临时购物车的key
        String tempCartKey = getCustomeCartKey(RedisConst.TEMP_ID_HEADER);
        //2）、得到用户购物车的key
        String userCartKey = getCustomeCartKey(RedisConst.USER_ID_HEADER);


        //2、用户没登录就是用临时购物车的所有数据
        if (userCartKey == null) {
            //给临时购物车设置过期时间
            Long expire = redisTemplate.getExpire(tempCartKey);
            if (expire < 0) {
                redisTemplate.expire(tempCartKey, 365, TimeUnit.DAYS);
            }
            List<CartInfo> cartItems = getCartItems(tempCartKey);
            return cartItems;
        }

        //3、用户登录要判断是否需要合并。
        try {
            Long tempSize = redisTemplate.opsForHash().size(tempCartKey);
            if (tempSize > 0) {
                //合并： 把临时购物车中每一个商品拿出来放到用户购物车
                List<CartInfo> tempItems = getCartItems(tempCartKey);
                for (CartInfo item : tempItems) {
                    addToCart(item.getSkuId(), item.getSkuNum(), userCartKey);
                }
                //合并结束，临时购物车删除
                redisTemplate.delete(tempCartKey);
            }
        }catch (Exception e){
            //说明合并期间出错。为了展示依然能进行必须把异常吃掉

        }


        List<CartInfo> cartItems = getCartItems(userCartKey);
        return cartItems;
    }

    private String getCustomeCartKey(String flag) {
        HttpServletRequest request = UserAuthUtil.request();
        String header = request.getHeader(flag);
        if (StringUtils.isEmpty(header)) {
            return null;
        }
        return RedisConst.CART_INFO_KEY + header;
    }
}
