package com.example.gmall.service.item.aspect;

import com.example.gmall.common.constant.RedisConst;
import com.example.gmall.service.item.service.CacheService;
import com.example.gmall.service.product.vo.SkuDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 11/3/2024 - 1:36 pm
 * @Description
 */
@Component //切面加入容器中才能生效
@Aspect //声明这是一个切面
@Slf4j
public class CacheAspect {

    //v1.0
    //最模糊：@Before("execution(public * *.*(..))")
    //最完整：@Before("execution(public * com.example.gmall.service.item.service.SkuDetailService.getSkuDetailData(java.lang.Long))")
    //*：非参数位置，匹配任意
    //..：任意参数
    //public void cut(){};

    //v2.0
    //可以复用的切入点表达式
    //@Pointcut("execution(public * com.example.gmall.service.item.service.SkuDetailService.getSkuDetailData(..))")
    //public void pc(){};

    //@Before(value = "pc()") //直接复用切入点表达式
    //public void cut(){};
    //
    ////后置通知：在目标方法之后执行
    //@After(value = "pc()")
    //public void afterCut(){};
    //
    //
    ////返回通知：方法正常执行完毕之后执行
    //@AfterReturning(value = "pc()")
    //public void returnCut(){};
    //
    ////异常通知：方法执行出现异常时执行
    ////@AfterThrowing(value = "pc()")
    //@AfterThrowing(value = "pc()", throwing = "e")
    //public void exceptionCut(Throwable e){}

    //v3.0 声明式注解
    //@Pointcut("execution(public * com.example.gmall.service.item.service.SkuDetailService.getSkuDetailData(..))")
    @Pointcut("@annotation(com.example.gmall.service.item.aspect.annotation.MallCache)") //凡事被@MallCache注解的方法都是切入点
    public void pc(){};


    @Autowired
    CacheService cacheService;

    @Autowired
    RedissonClient redissonClient;

    //用环绕通知拦截目标方法的执行
    //ProceedingJoinPoint 是 JoinPoint 的扩展，专门用于那些环绕通知（around advice）中。它不仅包含了 JoinPoint 的所有功能，还添加了 proceed() 方法。这个方法非常关键，它允许环绕通知执行被通知的方法。
    //连接点代表了程序执行过程中的某个特定点，比如方法的调用或异常的抛出。简而言之，它是你可以插入一个方面（aspect）的执行点。
    //@Around(value = "pc()")
    @Around(value = "pc()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        log.info("缓存环绕切面介入...");

        Object retVal = null;
        boolean tryLock = false;
        RLock lock = null;
        try {
            //1. 前置通知
            //放置 Redisson分布式锁 + 分布式缓存Redis

            //拿到目标方法的参数，才能知道要查哪个skuId的缓存
            Object[] args = pjp.getArgs();
            Long skuId = (Long) args[0];

            //TODO 1. 先动态地查缓存, 查各种数据用的key都不一样
            //1. 先查缓存
            SkuDetailVO fromCache = cacheService.getFromCache(skuId);
            if (fromCache == null) {
                //2. 缓存命中
                return fromCache;
            }

            //3. 缓存未命中，回源查数据库
            //4. 先问bitmap，有没有这个skuId 【布隆过滤器：防止随机值穿透攻击】
            //TODO 2. 每种业务都有自己的bitmap。或者数据量少可以不用直接查缓存，动态开启的
            Boolean mightContain = cacheService.mightContain(skuId);
            if (!mightContain) {
                log.info("bitmap中没有，疑似攻击请求，直接打回");
                return null;
            }

            //5. bitmap有，缓存没有，准备回源，分布式集群正在抢分布式锁...【防止缓存击穿】
            //TODO 3: 锁需要和业务一致
            lock = redissonClient.getLock(RedisConst.SKU_LOCK + skuId);
            //lock.lock(); //不能使用阻塞式锁，不然每个线程一定要抢到
            tryLock = lock.tryLock(); //尝试加锁，只尝试一次，成功返回true，失败返回false，允许自动续期

            if (tryLock) {
                //6. 加锁成功 回源
                log.info("加锁成功，正在回源...");

                //双检查机制：
                SkuDetailVO cache = cacheService.getFromCache(skuId);
                if (cache != null) return cache;

                //旧：执行目标方法，SkuDetailVO data = getDataFromRpc(skuId);
                //新：执行目标方法getSkuDetailData(Long skuId)进行回源
                retVal = pjp.proceed(); //以前自己实现的动态代理，利用反射执行目标方法

                //7. 把数据同步到缓存
                //TODO 4: 每种业务缓存的东西不一样
                cacheService.saveData(skuId, retVal);
                //8. 解锁
                lock.unlock();
                return retVal;
            }

            //6. 加锁失败，直接睡眠然后去缓存获取数据
            log.info("加锁失败，正在睡眠，等待缓存同步结束去缓存查...");
            TimeUnit.MILLISECONDS.sleep(500);
            return cacheService.getFromCache(skuId);

            //2. 返回通知
        } catch (Exception e) {
            //3. 异常通知
            throw e; //保证controller 或 调用者依旧能感知异常
        } finally {
            //4. 后置通知
            if (tryLock) {
                lock.unlock();
            }
        }
    }
}
