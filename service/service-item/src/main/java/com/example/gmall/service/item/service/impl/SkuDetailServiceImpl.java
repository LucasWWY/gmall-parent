package com.example.gmall.service.item.service.impl;


import com.example.gmall.common.constant.RedisConst;
import com.example.gmall.feign.product.ProductSkuDetailFeignClient;
import com.example.gmall.feign.search.SearchFeignClient;
import com.example.gmall.service.item.service.SkuDetailService;
import com.example.gmall.service.product.entity.SkuImage;
import com.example.gmall.service.product.entity.SkuInfo;
import com.example.gmall.service.product.entity.SpuSaleAttr;
import com.example.gmall.service.product.vo.CategoryTreeVO;
import com.example.gmall.service.product.vo.SkuDetailVO;
import com.example.gmall.service.product.vo.SkuDetailVO.CategoryViewDTO;
import com.example.gmall.starter.cache.aspect.annotation.MallCache;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 15/2/2024 - 4:07 pm
 * @Description
 */
@Service
@Slf4j
public class SkuDetailServiceImpl implements SkuDetailService {

    @Autowired
    ProductSkuDetailFeignClient productSkuDetailFeignClient;

    @Autowired
    SearchFeignClient searchFeignClient;

    @Autowired //自定义的线程池
    ThreadPoolExecutor coreExecutor;

    @Autowired
    StringRedisTemplate redisTemplate;

    //@Autowired
    //CacheService cacheService;

    @Autowired
    RedissonClient redissonClient;

    //5. 使用AOP切面拦截 Redisson分布式锁 + 分布式缓存Redis 各种东西全部放到切面中
    //业务只关注业务逻辑怎么实现，增强逻辑由切面实现
    @MallCache(
            cacheKey = RedisConst.SKU_DETAIL_CACHE + "#{#args[0]}",
            bitMapName = RedisConst.SKUID_BITMAP,
            bitMapIndex = "#{#args[0]}",
            lockKey = RedisConst.SKU_LOCK + "#{#args[0]}",
            ttl = 7,
            unit = TimeUnit.DAYS
    )
    @Override
    public SkuDetailVO getSkuDetailData(Long skuId) {
        return getDataFromRpc(skuId);
    }

    //4. Redisson分布式锁
    //public SkuDetailVO getSkuDetailDataWithDistLock(Long skuId) {
    //
    //    //1. 先查缓存
    //    SkuDetailVO cache = cacheService.getFromCache(skuId);
    //    if (cache != null) {
    //        //2. 缓存命中
    //        return cache;
    //    }
    //
    //    //3. 缓存未命中，回源查数据库
    //    //4. 先问bitmap，有没有这个skuId 【布隆过滤器：防止随机值穿透攻击】
    //    Boolean mightContain = cacheService.mightContain(skuId);
    //    if (!mightContain) {
    //        log.info("bitmap中没有，疑似攻击请求，直接打回");
    //        return null;
    //    }
    //    //5. bitmap有，缓存没有，准备回源，分布式集群正在抢锁...【防止缓存击穿】
    //    RLock lock = redissonClient.getLock(RedisConst.SKU_LOCK + skuId);
    //    //lock.lock(); //不能使用阻塞式锁，不然每个线程一定要抢到
    //    boolean tryLock = lock.tryLock(); //尝试加锁，只尝试一次，成功返回true，失败返回false，允许自动续期
    //    try {
    //        if (tryLock) {
    //            //6. 加锁成功 回源
    //            log.info("加锁成功，正在回源...");
    //            SkuDetailVO data = getDataFromRpc(skuId);
    //            //7. 把数据同步到缓存
    //            cacheService.saveData(skuId, data);
    //            //8. 解锁
    //            lock.unlock();
    //            return data;
    //        } else {
    //            //6. 加锁失败，直接睡眠然后去缓存获取数据
    //            log.info("加锁失败，正在睡眠，等待缓存同步结束去缓存查...");
    //            TimeUnit.MILLISECONDS.sleep(500);
    //            return cacheService.getFromCache(skuId);
    //        }
    //    } catch (Exception e) {
    //        return null;
    //    }
    //}

    ReentrantLock reentrantLock = new ReentrantLock(); //底层是AQS(AbstractQueuedSynchronizer) lock()底层是compareAndSetState() -> compareAndSwap() i.e. CAS
    //spring bean默认单例，实例中只有一把锁，所有线程都在竞争这一把锁，如果放在方法内部，每次调用方法时都会创建一个新的锁对象实例，意味着每个线程都会获得自己的锁对象
    //JUC本地锁，在分布式场景下，锁不住所有机器

    //3. 本地锁
    //public SkuDetailVO getSkuDetailDataWithLocalLock(Long skuId) {
    //    //1. 先查缓存
    //    SkuDetailVO fromCache = cacheService.getFromCache(skuId);
    //    //缓存未命中
    //    if (fromCache == null) {
    //        //2. 判断位图中是否有
    //        Boolean contain = cacheService.mightContain(skuId);
    //        if (!contain) {
    //            log.info("bitmap中没有，疑似攻击请求，直接打回");
    //            return null;
    //        }
    //
    //        //3. 商品存在 & 缓存未命中, 回源查数据库
    //        log.info("bitmap有，缓存没有，准备回源，正在抢锁...");
    //        //4. 拦截缓存击穿：抢锁
    //        //为什么在这拦？因为缓存击穿就是指热点数据失效后，大量请求会直接打到数据库，造成数据库压力大
    //        //而这正是高并发 请求数据库的地方
    //        boolean tryLock = reentrantLock.tryLock();//允许同一线程获得同一把锁
    //        if (tryLock) {
    //            //加锁成功（防止缓存击穿，就是只让一个人去查数据库并同步到缓存，其他人直接去缓存获取数据）
    //            log.info("加锁成功，正在回源...");
    //            SkuDetailVO data = getDataFromRpc(skuId);
    //            //5. 把数据同步到缓存
    //            cacheService.saveData(skuId, data); //为什么saveData中还缓存假数据(i.e."x" 防止缓存穿透)？因为布隆过滤器会误判，有不一定有，没有一定没有
    //            //假如布隆过滤器判断有，但实际上没有，那么就需要用假数据在缓存中占位，防止后续的查询缓存穿透
    //
    //            //6. 解锁
    //            reentrantLock.unlock();
    //            return data;
    //        } else {
    //            //加锁失败，直接睡眠然后去缓存获取数据（防止缓存击穿，就是只让一个人去查数据库并同步到缓存，其他人直接去缓存获取数据）
    //            log.info("加锁失败，正在睡眠，等待缓存同步结束去缓存查...");
    //            try {
    //                TimeUnit.MILLISECONDS.sleep(500);
    //                return cacheService.getFromCache(skuId);
    //            } catch (InterruptedException e) {
    //                throw new RuntimeException(e);
    //            }
    //        }
    //    } else {
    //        //缓存命中
    //        return fromCache;
    //    }
    //}

    //缓存：
    //1. 本地缓存：数据存放在微服务所在的jvm内存中
    private Map<Long, SkuDetailVO> cache = new ConcurrentHashMap<>(); //线程安全的哈希表
    //public SkuDetailVO getSkuDetailDataFromLocalCache(Long skuId) {
    //
    //    //1、先查缓存
    //    SkuDetailVO result = cache.get(skuId);
    //    //3、缓存没有；回源(i.e. 回到源头)查数据库
    //    if (result == null) {
    //        log.info("缓存未命中...回源");
    //        result = getDataFromRpc(skuId);
    //        //4、数据同步到缓存
    //        cache.put(skuId, result);
    //    }
    //
    //    return result;
    //}

    /**
     * 2. 分布式缓存(e.g. Redis)
     * @param skuId
     * @return
     */
    //public SkuDetailVO getSkuDetailDataNullSave(Long skuId) {
    //    //1. 先查缓存
    //    String jsonString = redisTemplate.opsForValue().get("skuInfo:" + skuId);
    //
    //    //2. 缓存未命中, 回源查数据库
    //    if (StringUtils.isEmpty(jsonString)) {
    //        synchronized (this) {
    //            //在Java中，每个对象都有一个关联的监视器锁（也称为内置锁或对象锁），使用synchronized关键字时，会获取对象的监视器锁
    //            //也就是会获取当前实例的监视器锁，而实例是跟skuId有关的，换句话说，对某特定商品加了锁，其他商品不受影响
    //            //但是如果100w请求，每一个请求的skuId都不一样，那么每一个请求都会加锁，这样就没有意义了，仍然会缓存穿透
    //            SkuDetailVO data = getDataFromRpc(skuId);
    //            jsonString = "x"; //防止缓存穿透（访问不存在数据），x是让不存在的数据也能存到缓存中
    //            //3. 把数据同步到缓存，即便是null也缓存，防止缓存穿透
    //            if (data != null) {
    //                jsonString = JSON.toJSONString(data);
    //            }
    //            redisTemplate.opsForValue().set("skuInfo:" + skuId, jsonString, 7, TimeUnit.DAYS);
    //            return data;
    //        }
    //    }
    //    //4. 缓存命中：
    //    // 4.2 混存假数据：应对缓存穿透，缓存穿透是指访问不存在的数据，导致每次都要访问数据库，这样会对数据库造成压力，所以把不存在数据也存到缓存中，屏蔽大量访问
    //    if ("x".equals(jsonString)) {
    //        log.info("疑似攻击请求");
    //        return null;
    //    }
    //    // 4.1 真数据
    //    SkuDetailVO skuDetailVO = JSON.parseObject(jsonString, SkuDetailVO.class);
    //    return skuDetailVO;
    //}


    private SkuDetailVO getDataFromRpc(Long skuId) {
        //CountDownLatch countDownLatch = new CountDownLatch(6);

        SkuDetailVO skuDetailVO = new SkuDetailVO();

        //我们这里需要的参数是三级分类的id，但是方法参数目前只有skuId，所以我们需要远程调用（i.e. skuDetailFeignClient）来获取三级分类的id
        //skuDetailFeignClient.getCategoryTreeWithC3Id();

        //2024.02.15 19:03
        //1. 获取sku的基本信息
        //2024.02.18 23:07
        //这些rpc基本没有先后顺序，获取详情页的数据合并成最终的skuDetailVO
        //所以使用异步，注意：异步前提：自定义的线程池，不要使用默认的线程池
        CompletableFuture<SkuInfo> skuInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfo skuInfo = productSkuDetailFeignClient.getSkuInfo(skuId).getData();
            //countDownLatch.countDown();
            return skuInfo;
        }, coreExecutor); //第二个参数是指定线程池，如果不声明，则使用默认的

        //2. 获取sku的图片信息
        CompletableFuture<Void> skuImageFuture = skuInfoCompletableFuture.thenAcceptAsync((skuInfo) -> {
            //与上面有先后关系，用thenAccept会复用上一步线程，res代表skuInfoCompletableFuture返回的结果，用thenAcceptAsync（i.e. 新开线程）
            if (skuInfo == null) return;
            List<SkuImage> skuImageList = productSkuDetailFeignClient.getSkuImages(skuId).getData();
            skuInfo.setSkuImageList(skuImageList);
            skuDetailVO.setSkuInfo(skuInfo);

            //countDownLatch.countDown();
        }, coreExecutor);

        //3. 当前商品精确完整分类信息
        CompletableFuture<Void> categoryViewFuture = skuInfoCompletableFuture.thenAcceptAsync((skuInfo) -> { //res代表skuInfoCompletableFuture返回的结果
            if (skuInfo == null) return;
            CategoryTreeVO categoryTreeVO = productSkuDetailFeignClient.getCategoryTreeWithC3Id(skuInfo.getCategory3Id()).getData();
            //得到CategoryTreeVO，需要CategoryViewDTO，所以类型要转换一下
            //BeanUtils.copyProperties(categoryTreeVO, categoryViewDTO); //用不了，因为CategoryTreeVO是自嵌套/递归的，需要手动转换
            CategoryViewDTO categoryViewDTO = convertToCategoryViewDTO(categoryTreeVO);
            skuDetailVO.setCategoryView(categoryViewDTO);

            //countDownLatch.countDown();
        }, coreExecutor);


        //4. 获取sku的价格
        CompletableFuture<Void> priceFuture = CompletableFuture.runAsync(() -> {
            BigDecimal price = productSkuDetailFeignClient.getPrice(skuId).getData();
            skuDetailVO.setPrice(price);

            //countDownLatch.countDown();
        }, coreExecutor);

        //5、销售属性
        CompletableFuture<Void> spuSaleAttrsFuture = skuInfoCompletableFuture.thenAcceptAsync((skuInfo) -> {
            if (skuInfo == null) return;
            List<SpuSaleAttr> spuSaleAttrs = productSkuDetailFeignClient.getSpuSaleAttr(skuInfo.getSpuId(), skuId).getData();
            skuDetailVO.setSpuSaleAttrList(spuSaleAttrs);

            //countDownLatch.countDown();
        }, coreExecutor);

        //6、当前sku的所有兄弟们的所有组合可能性。
        CompletableFuture<Void> valueJsonFuture = skuInfoCompletableFuture.thenAcceptAsync((skuInfo) -> {
            if (skuInfo == null) return;
            String jsonString = productSkuDetailFeignClient.getValuesSkuJson(skuInfo.getSpuId()).getData();
            skuDetailVO.setValuesSkuJson(jsonString);

            //countDownLatch.countDown();
        }, coreExecutor);

        //2024.02.18 23:07
        //异步情况
        //1 -> 2
        //  -> 3
        //4
        //  -> 5
        //  -> 6
        //2，3，4，5，6 可能几乎同时完成，所以不能直接返回，有可能skuDetailVO东西不全，所以要等待所有异步任务完成后再返回

        //方法一：
        //try {
        //    countDownLatch.await(); //countDownLatch.countDown(); 6次后，await()才会结束
        //} catch (InterruptedException e) {
        //    throw new RuntimeException(e);
        //}

        //方法二：
        CompletableFuture
                .allOf(valueJsonFuture, spuSaleAttrsFuture, priceFuture, categoryViewFuture, skuImageFuture)
                .join();
        //异步可能几乎同时完成，如果直接返回，那么有可能skuDetailVO有缺失数据的，所以要等待所有异步任务完成后再返回

        return skuDetailVO;
    }

    private CategoryViewDTO convertToCategoryViewDTO(CategoryTreeVO categoryTreeVO) {
        CategoryViewDTO categoryViewDTO = new CategoryViewDTO();
        categoryViewDTO.setCategory1Id(categoryTreeVO.getCategoryId());
        categoryViewDTO.setCategory1Name(categoryTreeVO.getCategoryName());

        CategoryTreeVO child1 = categoryTreeVO.getCategoryChild().get(0);
        categoryViewDTO.setCategory2Id(child1.getCategoryId());
        categoryViewDTO.setCategory2Name(child1.getCategoryName());

        CategoryTreeVO child2 = child1.getCategoryChild().get(0);
        categoryViewDTO.setCategory3Id(child2.getCategoryId());
        categoryViewDTO.setCategory3Name(child2.getCategoryName());

        return categoryViewDTO;
    }

    @Override
    public void incrHotScore(Long skuId) {
        //DB中没有这个field，只有ES中有hot score，所以需要远程调用service-search的ES，但是rpc调用太慢，所以不是每次加而是累计到一定数量再加
        //1. 累计热度
        Long score = redisTemplate.opsForValue().increment("sku:hotscore:" + skuId, 1); //应该是每次+1
        if (score%100 == 0) {
            //2. 达到一定数量，调用ES更新hot score
            searchFeignClient.updateHotScore(skuId, score);
        }

    }
}
