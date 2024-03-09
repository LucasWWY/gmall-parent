package com.example.gmall.service.item.controller;

import com.example.gmall.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 1/3/2024 - 6:06 pm
 * @Description
 */
@Slf4j
@RestController
public class RedissonController {

    @Autowired
    RedissonClient redissonClient;

    @GetMapping("/redisson/lock_v1")
    public Result lock_v1(){
        //1. 获取一把锁
        RLock lock = redissonClient.getLock("haha-lock"); //默认非公平锁 保证线程安全 只要锁名一样 就是同一把锁

        //2. 加锁
        try {
            //1、阻塞式加锁 + 自旋锁 一定要等到锁 锁的默认TTL是30s 业务执行期间自动续期
            lock.lock();
            //2、阻塞式加锁(传了锁释放时间) 一定要等到锁 锁的默认时间5s 没有续期功能
            lock.lock(5, TimeUnit.SECONDS);
            //如果锁的释放时间<业务执行时间 即锁在业务执行完毕之前释放，释放锁找不到会报错

            //但是阻塞式加锁会一直等待，性能不好

            //模拟业务执行
            TimeUnit.SECONDS.sleep(60);
        } catch (InterruptedException e) {

        } finally {
            //3. 解锁
            try {
                lock.unlock();
                //lock.unlockAsync();
            } catch (Exception e) { //因为锁有TTL，如果不自动续期，可能会解到别人的锁

            }
            return Result.ok();
        }
    }

    @GetMapping("/redisson/lock_v2")
    public Result lock_v2(){
        //1. 获取一把锁
        RLock lock = redissonClient.getLock("haha-lock"); //默认非公平锁 保证线程安全 只要锁名一样 就是同一把锁

        //2. 加锁
        try {
            //阻塞式加锁会一直等待，性能不好

            //1. 尝试加锁，只尝试一次，成功返回true，失败返回false，拿到锁后，业务处理期间自动续期
            boolean tryLock = lock.tryLock();
            //2. 尝试加锁，10s内一直尝试
            lock.tryLock(10, TimeUnit.SECONDS);
            //3. 尝试加锁，1s内一直尝试，锁的有效时间是10s 不自动续期
            lock.tryLock(1, 10, TimeUnit.SECONDS);

            if (tryLock) {
                //模拟业务执行
                TimeUnit.SECONDS.sleep(60);
            } else {
                return Result.fail().message("加锁失败");
            }
        } catch (InterruptedException e) {

        } finally {
            //3. 解锁
            try {
                lock.unlock();
                //lock.unlockAsync();
            } catch (Exception e) { //因为锁有TTL，如果不自动续期，可能会解到别人的锁

            }
            return Result.ok();
        }
    }

    String x = "abc";

    /**
     * 读写锁： 改数据
     * 读读： 无锁 （注意测试的时候浏览器可能导致问题，同一个浏览器多个tab请求，请求会一个一个被处理，最终呈现出加锁的效果，不同浏览器不会有问题）
     * 写写： 有锁
     * 写读： 有锁，读要等待
     * 读写： 有锁，写要等待
     * @return
     */
    @GetMapping("/write")
    public Result write() throws InterruptedException {
        //1、获取读写锁
        RReadWriteLock rwLock = redissonClient.getReadWriteLock("hahahaha");

        //2、获取写锁
        RLock wLock = rwLock.writeLock();

        //3、加写锁
        try {
            wLock.lock();
            log.info("业务正在进行数据修改....");
            TimeUnit.SECONDS.sleep(15);
            x = UUID.randomUUID().toString();
            log.info("业务数据修改完成....");
        }finally {
            try {
                wLock.unlock();
            }catch (Exception e){}
        }

        return Result.ok(x);
    }

    @GetMapping("/read")
    public Result read() throws InterruptedException {
        //1、获取读写锁
        RReadWriteLock rwLock = redissonClient.getReadWriteLock("hahahaha");

        //2、获取读锁
        RLock rLock = rwLock.readLock();

        //3、加读锁
        try { //防止加锁时和redis服务器连接断开
            rLock.lock();
            log.info("业务正在远程读取数据中....");
            TimeUnit.SECONDS.sleep(10);
            log.info("业务远程读取数据完成....");
        } finally {
            rLock.unlock();
        }
        return Result.ok(x);
    }

    /**
     * 闭锁； 只能减，减到0结束
     * 收集龙珠
     */
    @GetMapping("/shenlong")
    public Result shenlong(HttpServletRequest request) throws InterruptedException {
        String remoteAddr = request.getRemoteAddr();
        log.info("远程访问/shenlong：{}",remoteAddr);

        RCountDownLatch qilongzhu = redissonClient.getCountDownLatch("qilongzhu");
        qilongzhu.trySetCount(7L);

        log.info("等待召唤....");
        qilongzhu.await();

        return Result.ok("神龙.....");
    }

    @GetMapping("/longzhu")
    public Result longzhu(HttpServletRequest request){
        String remoteAddr = request.getRemoteAddr();
        log.info("远程访问/longzhu：{}",remoteAddr);

        RCountDownLatch qilongzhu = redissonClient.getCountDownLatch("qilongzhu");
        qilongzhu.countDown();

        return Result.ok("1颗到手...");
    }

    /**
     * 信号量； 能增能减
     * 停车场
     */
    @GetMapping("/init/park")
    public Result Semaphore(HttpServletRequest request){
        String remoteAddr = request.getRemoteAddr();
        log.info("远程访问init/park：{}",remoteAddr);

        RSemaphore semaphore = redissonClient.getSemaphore("carpark");
        semaphore.trySetPermits(5); //5个车位

        return Result.ok("初始化停车场完成...");
    }

    @GetMapping("/car/park")
    public Result park(HttpServletRequest request) throws InterruptedException {
        String remoteAddr = request.getRemoteAddr();
        log.info("远程访问car/park：{}",remoteAddr);

        RSemaphore semaphore = redissonClient.getSemaphore("carpark");
        semaphore.acquire(1); //停一个车

        return Result.ok("停车成功");
    }

    //如果请求car/park五次，CountDownLatch的值会变成0，然后继续请求car/move，会等待，car/move请求，会释放一个车位，然后第六次car/park请求会返回

    @GetMapping("/car/move")
    public Result move(HttpServletRequest request) throws InterruptedException {
        String remoteAddr = request.getRemoteAddr();
        log.info("远程访问car/move：{}",remoteAddr);

        RSemaphore semaphore = redissonClient.getSemaphore("carpark");
        semaphore.release(1); //走一个车，但是value可以超过初始化设置的5

        return Result.ok("车走了...");
    }

}
