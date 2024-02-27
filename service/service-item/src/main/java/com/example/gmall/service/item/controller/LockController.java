package com.example.gmall.service.item.controller;

import com.example.gmall.common.result.Result;
import com.example.gmall.service.item.service.LockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 26/2/2024 - 12:05 pm
 * @Description
 */
@Slf4j
@RestController //容器中只有一个这个对象。
public class LockController {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    LockService lockService;

    int i = 0;  //有风险

    CountDownLatch latch = new CountDownLatch(10000);


    //2，分布式锁
    /**
     * 1）、并发情况操作共享数据会产生安全问题
     * 可转化成：
     * 2）、多线程操作共享数据会产生线程安全问题
     *
     * 发1w请求测试：
     * 1、无锁、单实例情况：    预期：1w  实际：239      吞吐量 7000+/s
     * 2、本地锁、单实例情况：  预期：1w  实际：10000     吞吐量 933/s
     * 3、本地锁、集群情况：   预期：1w   实际：4225     吞吐量 1500+/s (jmeter请求要发给gateway, gateway再lb给集群中的item服务)
     * 本地锁：解决了本地多线程的安全问题，但是分布式情况下无法解决，三台机器统一修改redis的值，最终值4225接近1w的1/3
     * 4、分布式锁、集群情况：  预期：1w  实际：1w       吞吐量 275/s
     * 分布式锁：解决了分布式情况下的安全问题，但是性能较差
     * 效果：
     * 1）、分布式下如果保证某个业务必须同一时刻只有一个人在执行就得用分布式锁
     * 2）、分布式锁在分布式情况下能锁住所有人
     * 3）、锁越大性能越差
     * @return
     */
    @GetMapping("/incr")
    public Result incrWithDistLock() throws InterruptedException {
        //分布式锁：利用setnx命令，让redis在没有这个key的时候插入这个key，有就无操作

        //得到一个自旋锁
        String uuid = lockService.lock();

        String num = redisTemplate.opsForValue().get("num");
        //2、对这个数字进行加1
        num = Integer.parseInt(num) + 1 + "";
        //3、加完以后修改给远程
        redisTemplate.opsForValue().set("num", num);
        //解锁
        lockService.unlock(uuid); //解锁

        return Result.ok();
    }


    //1. 本地锁
    ReentrantLock lock = new ReentrantLock();
    //spring bean默认单例，实例中只有一把锁，所有线程都在竞争这一把锁，如果放在方法内部，线程每次调用方法时都会创建一个新的锁对象实例，意味着每个线程都会获得自己的锁对象

    public Result incrWithLocalLock(){
        //i++;
        //1、读取i值
        //2、对i+1
        //3、重新赋值i

        //latch.countDown(); //统计请求数量

        //1、从远程获取一个数字
        lock.lock();
        String num = redisTemplate.opsForValue().get("num");
        //2、对这个数字进行加1
        num = Integer.parseInt(num) + 1 + "";
        //3、加完以后修改给远程
        redisTemplate.opsForValue().set("num",num);
        lock.unlock();

        return Result.ok();
    }


    @GetMapping("/result")
    public Result testCountDownLatch() throws InterruptedException {

        latch.await();
        return Result.ok(i);
    }
}

