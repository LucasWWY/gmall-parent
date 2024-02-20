package com.example.gmall.service.item.service.impl;

import com.example.gmall.service.item.feign.SkuDetailFeignClient;
import com.example.gmall.service.item.service.SkuDetailService;
import com.example.gmall.service.product.entity.SkuImage;
import com.example.gmall.service.product.entity.SkuInfo;
import com.example.gmall.service.product.entity.SpuSaleAttr;
import com.example.gmall.service.product.vo.CategoryTreeVO;
import com.example.gmall.service.product.vo.SkuDetailVO;
import com.example.gmall.service.product.vo.SkuDetailVO.CategoryViewDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 15/2/2024 - 4:07 pm
 * @Description
 */
@Service
public class SkuDetailServiceImpl implements SkuDetailService {

    @Autowired
    SkuDetailFeignClient skuDetailFeignClient;

    @Autowired //自定义的线程池
    ThreadPoolExecutor coreExecutor;

    //缓存
    private Map<Long, SkuDetailVO> cache = new ConcurrentHashMap<>();

    @Override
    public SkuDetailVO getSkuDetailData(Long skuId) {
        return getDataFromRpc(skuId);
    }

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
            SkuInfo skuInfo = skuDetailFeignClient.getSkuInfo(skuId).getData();
            //countDownLatch.countDown();
            return skuInfo;
        }, coreExecutor); //第二个参数是指定线程池，如果不声明，则使用默认的

        //2. 获取sku的图片信息
        CompletableFuture<Void> skuImageFuture = skuInfoCompletableFuture.thenAcceptAsync((skuInfo) -> {
            //与上面有先后关系，用thenAccept会复用上一步线程，res代表skuInfoCompletableFuture返回的结果，用thenAcceptAsync（i.e. 新开线程）
            List<SkuImage> skuImageList = skuDetailFeignClient.getSkuImages(skuId).getData();
            skuInfo.setSkuImageList(skuImageList);
            skuDetailVO.setSkuInfo(skuInfo);

            //countDownLatch.countDown();
        }, coreExecutor);

        //3. 当前商品精确完整分类信息
        CompletableFuture<Void> categoryViewFuture = skuInfoCompletableFuture.thenAcceptAsync((skuInfo) -> { //res代表skuInfoCompletableFuture返回的结果
            CategoryTreeVO categoryTreeVO = skuDetailFeignClient.getCategoryTreeWithC3Id(skuInfo.getCategory3Id()).getData();
            //得到CategoryTreeVO，需要CategoryViewDTO，所以类型要转换一下
            //BeanUtils.copyProperties(categoryTreeVO, categoryViewDTO); //用不了，因为CategoryTreeVO是自嵌套/递归的，需要手动转换
            CategoryViewDTO categoryViewDTO = convertToCategoryViewDTO(categoryTreeVO);
            skuDetailVO.setCategoryView(categoryViewDTO);

            //countDownLatch.countDown();
        }, coreExecutor);


        //4. 获取sku的价格
        CompletableFuture<Void> priceFuture = CompletableFuture.runAsync(() -> {
            BigDecimal price = skuDetailFeignClient.getPrice(skuId).getData();
            skuDetailVO.setPrice(price);

            //countDownLatch.countDown();
        }, coreExecutor);

        //5、销售属性
        CompletableFuture<Void> spuSaleAttrsFuture = skuInfoCompletableFuture.thenAcceptAsync((skuInfo) -> {
            List<SpuSaleAttr> spuSaleAttrs = skuDetailFeignClient.getSpuSaleAttr(skuInfo.getSpuId(), skuId).getData();
            skuDetailVO.setSpuSaleAttrList(spuSaleAttrs);

            //countDownLatch.countDown();
        }, coreExecutor);

        //6、当前sku的所有兄弟们的所有组合可能性。
        CompletableFuture<Void> valueJsonFuture = skuInfoCompletableFuture.thenAcceptAsync((skuInfo) -> {
            String jsonString = skuDetailFeignClient.getValuesSkuJson(skuInfo.getSpuId()).getData();
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
}
