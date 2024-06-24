package com.example.gmall.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.gmall.cart.entity.CartItem;
import com.example.gmall.common.authen.UserAuthUtils;
import com.example.gmall.common.constant.RedisConst;
import com.example.gmall.common.execption.GmallException;
import com.example.gmall.common.result.ResultCodeEnum;
import com.example.gmall.feign.product.ProductSkuDetailFeignClient;
import com.example.gmall.service.CartService;
import com.example.gmall.service.product.entity.SkuInfo;
import com.example.gmall.user.vo.UserAuthInfoVO;
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
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
    public SkuInfo addToCart(Long skuId, Integer skuNum, String cartKey) {
        //1. 获取当前用户信息 工具类方法调用也是同一个线程
        //UserAuthInfoVO info = UserAuthUtils.getUserAuthInfo();
        //Long userId = info.getUserId();
        //Long userTempId = info.getUserTempId();

        //2. 得到redis中购物车用的key：cart:info:拼userId还是userTempId取决于登没登录
        //String cartKey = determinCartKey(); //前两步都封装到这里面去
        //cart:info:3 hash结构 (49: skuInfo)

        //从redis拿到商品：以前购物车是否有这个商品
        CartItem item = getItem(cartKey, skuId);
        //1、没有：添加
        if (item == null) {
            item = prepareCartItem(skuId, skuNum); //远程调用service-product得到商品信息
            //保存到redis
            saveItem(cartKey, item);
            //给前端准备返回用的数据
            SkuInfo skuInfo = convertItemToSkuInfo(item);
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
    public String determineCartKey() {

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
    public CartItem getItem(String cartKey, Long skuId) {
        //1、拿到商品
        Object item = redisTemplate.opsForHash()
                .get(cartKey, skuId.toString()); //hash结构 hash的key是skuId
        if (item == null) {
            return null;
        }

        String json = item.toString(); //redis本身存储的是序列化的字节数据
        //2、如果有，就逆转
        CartItem info = JSON.parseObject(json, CartItem.class);
        return info;
    }

    private CartItem prepareCartItem(Long skuId, Integer num) {
        SkuInfo skuInfo = skuDetailFeignClient.getSkuInfo(skuId).getData();
        CartItem item = new CartItem();
        item.setSkuId(skuInfo.getId());
        item.setCartPrice(skuInfo.getPrice());
        item.setSkuPrice(skuInfo.getPrice());
        item.setSkuNum(num);
        item.setImgUrl(skuInfo.getSkuDefaultImg());
        item.setSkuName(skuInfo.getSkuName());
        item.setIsChecked(1);
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
        return item;
    }

    @Override
    public void saveItem(String cartKey, CartItem item) {

        //1、购物车单个商品不超200
        if (item.getSkuNum() >= RedisConst.CART_ITEM_NUM_LIMIT) {
            throw new GmallException(ResultCodeEnum.CART_ITEM_NUM_OVERFLOW);
        }

        //2、一个购物车不同商品的种类不超过200
        Long size = redisTemplate.opsForHash().size(cartKey); //199
        //BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartKey); //=操作绑定了cartKey的hashOps，也就可以看做得到了一个购物车

        Boolean hasKey = redisTemplate.opsForHash()
                .hasKey(cartKey, item.getSkuId().toString()); //这个商品是否已经存在
        if (!hasKey) { //不存在才要加1
            if (size + 1 >= 200) { //考虑加入种类的item进去会不会超过使得总种类200
                throw new GmallException(ResultCodeEnum.CART_ITEM_COUNT_OVERFLOW);
            }
        }

        redisTemplate.opsForHash()
                .put(cartKey, item.getSkuId().toString(), JSON.toJSONString(item));

    }

    private SkuInfo convertItemToSkuInfo(CartItem itemToSave) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setSkuName(itemToSave.getSkuName());
        skuInfo.setSkuDefaultImg(itemToSave.getImgUrl());
        skuInfo.setId(itemToSave.getSkuId());
        return skuInfo;
    }

    /**
     * 展示购物车中所有商品（牵扯到购物车合并）
     * @return
     */
    @Override
    public List<CartItem> displayItems() {
        //1、先得到临时 和 用户购物车的cartKey
        //determineCartKey()方法中已经封装了这个逻辑，但是它不能区分是userId还是UserTempId
        //1）、得到临时购物车的key
        String tempCartKey = getCartKey(RedisConst.USER_TEMP_ID_HEADER);
        //2）、得到用户购物车的key
        String userCartKey = getCartKey(RedisConst.USER_ID_HEADER);

        //2、用户没登录就是用临时购物车的所有数据
        if (userCartKey == null) {
            //给临时购物车设置过期时间
            Long expire = redisTemplate.getExpire(tempCartKey);
            if (expire < 0) { //expire = -1 means permanent
                redisTemplate.expire(tempCartKey, 365, TimeUnit.DAYS);
            }
            return getCartItems(tempCartKey);
        }

        //3、用户登录要判断是否需要合并
        //不管何时userTempId都是有，这也是为什么前面只判断userCartKey
        try {
            Long tempSize = redisTemplate.opsForHash().size(tempCartKey);
            if (tempSize > 0) { //检查临时购物车中是否有东西
                //合并： 把临时购物车中每一个商品拿出来放到用户购物车
                List<CartItem> tempItems = getCartItems(tempCartKey);
                for (CartItem item : tempItems) {
                    addToCart(item.getSkuId(), item.getSkuNum(), userCartKey); //借助userCartKey向用户购物车添加临时购物车的item
                    //addToCart已经封装了处理 已经有 / 没有 item的逻辑 已有的可以直接覆盖
                }
                //合并结束，临时购物车删除
                redisTemplate.delete(tempCartKey);
            }
        }catch (Exception e){
            //说明合并期间出错 e.g.超过200。但不管怎样都必须继续显示之前的list
        }
        List<CartItem> cartItems = getCartItems(userCartKey);

        return cartItems;
    }

    private String getCartKey(String flag) {
        HttpServletRequest request = UserAuthUtils.getRequest();
        //String userId = request.getHeader(RedisConst.USER_ID_HEADER);
        //String userTempId = request.getHeader(RedisConst.USER_TEMP_ID_HEADER);
        String id = request.getHeader(flag);
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        return RedisConst.CART_INFO + id; //这里是为了能区分userId userTempId后面要分别处理，所以没用determineCartKey
    }


    @Override
    public List<CartItem> getCartItems(String cartKey) {

        List<CartItem> items = redisTemplate.opsForHash()
                .values(cartKey) //根据cartKey得到value，也就是item的key
                .stream()
                .map(item -> JSON.parseObject(item.toString(), CartItem.class))
                //.sorted(Comparator.comparing(CartItem::getCreateTime))
                .sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()))
                .collect(Collectors.toList());

        //同步最新价格
        //TODO 这里会有Bug，如果点击删除选中的商品，会先调用getCartItems,这样就会触发后台异步的同步价格，
        // 结果导致删除的东西重新保存到redis，删不掉的情况； putIfPresent
        //解决：使用 putIfPresent操作即可

        CompletableFuture.runAsync(()->{
            syncPrice(cartKey, items); //里面有远程调用skuDetailFeignClient.getPrice，比较耗时，不能让后面的return等着，所以要异步价格同步
        },executor); //如果直接使用 CompletableFuture.runAsync 而不指定线程池，默认情况下它会使用全局的 ForkJoinPool 这可能会导致系统中的并发任务数量难以控制，特别是在高并发场景中，可能会导致线程争用和资源耗尽。
        //通过自定义线程池，可以：
        //1. 限制线程数量： 控制并发任务的数量，防止线程资源被耗尽。
        //2. 配置线程池参数： 根据业务需求配置线程池的核心线程数、最大线程数、队列长度等。

        return items; //但有个小问题，因为syncPrice是异步的，在其同步sql redis价格之前，就已经先返回了东西，所以用户第一次是看不到价格变化的，需要刷新，但是无伤大雅，价格真正严格一致是在order界面
    }

    @Override
    public void updateItemNum(String cartKey, Long skuId, Integer num) {
        //1、获取购物车商品
        CartItem cartItem = getItem(cartKey, skuId);
        if (num == 1 || num == -1) {
            cartItem.setSkuNum(cartItem.getSkuNum() + num);
        } else {
            //直接双击修改数量
            cartItem.setSkuNum(num);
        }
        //2、保存到redis
        saveItem(cartKey, cartItem);
    }

    @Override
    public void checkItem(String cartKey, Long skuId, Integer checked) {
        if (!(checked == 1 || checked == 0)) {
            throw new GmallException(ResultCodeEnum.INVAILD_PARAM);
        }

        CartItem item = getItem(cartKey, skuId);
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
        List<CartItem> checkeds = getCheckeds(cartKey);
        List<String> collect = checkeds
                .stream()
                .map(item -> item.getSkuId().toString())
                .collect(Collectors.toList());
        //2、如果删除很快运行完成，而同步价格后台线程慢慢运行，就会导致删不掉
        redisTemplate.opsForHash().delete(cartKey, collect.toArray());
    }

    @Override
    public List<CartItem> getCheckeds(String cartKey) {
        //直接调用这个会有bug
//        List<CartInfo> cartItems = getCartItems(cartKey);
        List<CartItem> collect = redisTemplate.opsForHash()
                .values(cartKey)
                .stream()
                .map(item -> JSON.parseObject(item.toString(), CartItem.class))
                .sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()))
                .filter((o1) -> o1.getIsChecked() == 1)//
                .collect(Collectors.toList());
        return collect;
    }

    /**
     * 如何保证最新价格：
     * 1. 购物车确实在redis中缓存最新价格，可能有时会和数据库稍微不一样
     * 2. 购物车展示列表的时候，再查询一下价格，价格一致，但是性能差
     * 上下架状态同步：
     *
     * @param cartKey
     * @param items
     */
    private void syncPrice(String cartKey, List<CartItem> items) {
        //节流代码
        //Long increment = redisTemplate.opsForValue().increment("price:"+cartKey);
        //if(increment%10 == 0){
        //
        items
                .stream()
                //.parallel() //并发，但是ForkJoinPool无限大，仍有OOM风险
                .forEach(item->{
                    //远程查询价格
                    BigDecimal realPrice = skuDetailFeignClient.getPrice(item.getSkuId()).getData();
                    if (Math.abs(item.getSkuPrice().doubleValue() - realPrice.doubleValue()) >= 0.0001) {
                        //说明价格发生了变化
                        //log.info("购物车：{}，中的商品{}，价格发生变化，最新为：{}", cartKey,item.getSkuId(),realPrice);
                        item.setSkuPrice(realPrice);
                        saveItem(cartKey,item);
                    }
                });

    }

    /
    public static void concurrentTest(String[] args) {
        //1. 非并发
        System.out.println("a");
        Arrays.asList(1,2,3,4,5,6)
                .stream()
                .forEach(item->{
                    System.out.println(Thread.currentThread()+">："+item);
                });
        System.out.println("b");
        //a 123456 b

        //2. 并发
        System.out.println("a");
        Arrays.asList(1,2,3,4,5,6)
                .stream()
                .parallel()
                .forEach(item->{
                    System.out.println(Thread.currentThread()+">："+item);
                });
        System.out.println("b");
        //a 451623 b



    }


}
