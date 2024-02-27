package com.example.gmall.service.item.service.impl.impl;

import com.example.gmall.service.item.service.LockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 26/2/2024 - 3:39 pm
 * @Description
 */
@Service
public class LockServiceImpl implements LockService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public String lock() {
        //分布式锁：利用setnx命令，让redis在没有这个key的时候插入这个key，有就无操作

        //分布式锁：防止删除别人的锁
        String uuid = UUID.randomUUID().toString();

        //如果不存在则set一个值
        while (!redisTemplate.opsForValue().setIfAbsent("lock", uuid, 10, TimeUnit.SECONDS)) {
            //自旋锁：一种用于多线程同步的锁，它在尝试获取锁的过程中会不断地循环检查锁是否可用，而不是让线程立即进入休眠或阻塞状态。这种方式在等待锁的时间很短的情况下可以减少线程上下文切换的开销，提高系统的并发性能
            //加锁成功后
            try {
                TimeUnit.MILLISECONDS.sleep(10); //不休眠一直自旋，会导致cpu飙升
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        //redisTemplate.expire("lock", 10, TimeUnit.SECONDS); //设置过期时间，防止死锁
        return uuid;
    }

    //2024.2.27 23:18
    //使用Redission解决 分布式锁 + 事务

    //2024.2.27 23:00
    @Override
    public void unlock(String uuid) {
        //通过lua脚本获得原子性
        String script = "if redis.call('get', KEYS[1]) == ARGV[1]\n" +
                "then\n" +
                "   return redis.call('del', KEYS[1])\n" +
                "else\n" +
                "   return 0\n" +
                "end";
        //问题：lua脚本保证原子性，但是保证事务吗？不保证，如果lua含有各种修改命令，那么执行过程中断电，会崩
        //所以lua脚本不要写超长而且还加各种修改，会导致事务问题
        //解决：开启redis事务机制，让lua脚本在事务内执行，但是redis事务开启性能下降很多，不如mysql事务

        Long result = redisTemplate.execute(
                new DefaultRedisScript<>(script, Long.class),
                Arrays.asList("lock"),
                uuid
        );
    }

    //2024.2.27 22:30
    public void unlockNotAtomic(String uuid) {
        //1. 得到锁值；这个值有可能跟我们加锁时用的值不一样(锁过期redis自动删除，别人占上就是别的值)
        if (uuid == redisTemplate.opsForValue().get("lock")) {
            //2. 锁没有变化则可以删除
            redisTemplate.delete("lock");
            //比对和删除是两行，也就是分步执行 获取锁值的时候 确实没过期 就是旧值，结果等到发送del命令的时候，锁过期了，直接删除锁导致删除了别人的锁
        }
    }

}
