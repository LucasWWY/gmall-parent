package com.example.gmall.service.product.init;

import com.example.gmall.common.constant.RedisConst;
import com.example.gmall.service.product.entity.SkuInfo;
import com.example.gmall.service.product.service.SkuInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 23/2/2024 - 8:37 pm
 * @Description
 */
@Slf4j
@Component
public class InitRunner implements CommandLineRunner {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    SkuInfoService skuInfoService;

    public void initBitMap() {
        //启动构建包含所有skuid的位图
        log.info("正在初始化 skuid - bitmap");
        List<SkuInfo> ids = skuInfoService.lambdaQuery()
                .select(SkuInfo::getId)
                .list(); //返回的是符合查询条件的完整的 SkuInfo 对象列表,id被封装,不是单纯的id列表


        ids.stream()
                .forEach(item -> {
                    redisTemplate.opsForValue().setBit(RedisConst.SKUID_BITMAP, item. getId(), true);
                });
        log.info("skuid - bitmap初始化完成");
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("InitRunner...项目启动自动执行run方法...初始化数据...组件：{}", skuInfoService);
        initBitMap();
    }
}
