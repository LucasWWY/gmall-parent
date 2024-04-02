package com.example.gmall.common.cache.aspect;

import com.example.gmall.common.cache.aspect.annotation.MallCache;
import com.example.gmall.common.cache.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
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
    @Pointcut("@annotation(com.example.gmall.common.cache.aspect.annotation.MallCache)") //凡事被@MallCache注解的方法都是切入点
    public void pc(){};

    @Autowired
    CacheService cacheService;

    @Autowired
    RedissonClient redissonClient;

    //1、创建表达式解析器
    SpelExpressionParser parser = new SpelExpressionParser();

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

            //24/3/5: 拿到目标方法的参数，才能知道要查哪个skuId的缓存
            //Object[] args = pjp.getArgs();
            //Long skuId = (Long) args[0];
            //24/3/16: 引入了SpEL表达式

            //TODO 1. 先动态地查缓存, 查不同业务用的key都不一样
            //1. 先查缓存
            Type returnType = getMethodReturnType(pjp);
            //SkuDetailVO fromCache = cacheService.getFromCache(skuId); //getFromCache()针对skuDetailVO写死的
            //Object fromCache = cacheService.getCacheData(RedisConst.SKU_DETAIL_CACHE + skuId, returnType); //查缓存通用，但key写死了，我可以在@MallCache中添加缓存key参数
            MallCache mallCache = getMethodAnnotation(pjp, MallCache.class); //读取@MallCache注解的参数，通过SpEL获取缓存key

            String cacheKey = "";
            if(StringUtils.isEmpty(mallCache.cacheKey())){ //默认值为""
                cacheKey = getCacheKeyDefault(mallCache.cacheKey(),pjp);
            }else {
                cacheKey = evalExpression(mallCache.cacheKey(), pjp,String.class); //用了SpEL，需要解析
            }

            Object fromCache = cacheService.getCacheData(cacheKey, returnType);//通用的key + 通用的查缓存

            if (fromCache == null) {
                //2. 缓存命中
                return fromCache;
            }

            //3. 缓存未命中，回源查数据库
            //4. 先问bitmap，有没有这个skuId 【布隆过滤器：防止随机值穿透攻击】
            //TODO 2. 每种业务都有自己的bitmap。或者数据量少可以不用直接查缓存，动态开启
            //Boolean mightContain = cacheService.mightContain(skuId);
            String bitMapName = mallCache.bitMapName(); //获得注解的参数
            if (!StringUtils.isEmpty(bitMapName)) { //数据量少 e.g. 三级分类 不用位图
                Assert.hasLength(mallCache.bitMapIndex(), "位图索引位置必须给定"); //给了bitMapName说明要用bitmap，但是不给index就无法判断是否存在
                Long bitMapIndex = evalExpression(mallCache.bitMapIndex(), pjp, Long.class);
                Boolean mightContain = cacheService.mightContain(bitMapName, bitMapIndex); //使用哪个位图，查哪个位置

                if (!mightContain) {
                    log.info("bitmap中没有，疑似攻击请求，直接打回");
                    return null;
                }
            } //不用位图，则直接去拿锁

            //5. bitmap有，缓存没有，准备回源，分布式集群正在抢分布式锁...【防止缓存击穿】
            //TODO 3: 锁需要和业务一致
            //lock = redissonClient.getLock(RedisConst.SKU_LOCK + skuId);

            if(StringUtils.isEmpty(mallCache.lockKey())) {
                lock = redissonClient.getLock("lock:" + cacheKey); //没给锁，但是不能没锁，所以要自动创建锁
            } else {
                String lockKey = evalExpression(mallCache.lockKey(), pjp, String.class);
                lock = redissonClient.getLock(lockKey);
            }
            //lock.lock(); //不能使用阻塞式锁，不然每个线程一定要抢到
            tryLock = lock.tryLock(); //尝试加锁，只尝试一次，成功返回true，失败返回false，允许自动续期

            if (tryLock) {
                //6. 加锁成功 回源
                log.info("加锁成功，正在回源...");

                //双检查机制：
                fromCache = cacheService.getCacheData(cacheKey, returnType);
                if (fromCache != null) return fromCache;

                //旧：执行目标方法，SkuDetailVO data = getDataFromRpc(skuId);
                //新：执行目标方法getSkuDetailData(Long skuId)进行回源
                retVal = pjp.proceed(); //以前自己实现的动态代理，利用反射执行目标方法

                //7. 把数据同步到缓存
                //TODO 4: 每种业务缓存的东西不一样
                //cacheService.saveData(skuId, retVal);
                cacheService.saveCacheData(cacheKey, retVal, mallCache.ttl(), mallCache.unit());
                //8. 解锁
                lock.unlock();
                return retVal;
            }

            //6. 加锁失败，直接睡眠然后去缓存获取数据
            log.info("加锁失败，正在睡眠，等待缓存同步结束去缓存查...");
            TimeUnit.MILLISECONDS.sleep(500);
            return cacheService.getCacheData(cacheKey, returnType);

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

    private String getCacheKeyDefault(String cacheKey, ProceedingJoinPoint pjp) {
        //没传表达式； 默认使用方法全签名 + 参数列表
        MethodSignature signature = (MethodSignature) pjp.getSignature();

        String methodName = signature.getMethod().getName();
        String className = signature.getMethod().getDeclaringClass().toString().replace("class ", ""); //class java.lang.String 去掉class多余字符
        Object params = Arrays.stream(pjp.getArgs())
                .reduce((o1, o2) -> o1.toString() + "_" + o2.toString())
                .get();
        //缓存的key = 类名：方法名：参数表
        return className+":"+methodName+":"+params.toString();
    }

    private <T> T evalExpression(String expr, ProceedingJoinPoint pjp, Class<T> returnType) {
        //2、解析表达式
        Expression expression = parser.parseExpression(expr, ParserContext.TEMPLATE_EXPRESSION);

        //3、得到表达式值
        EvaluationContext ec = new StandardEvaluationContext();
        ec.setVariable("args", pjp.getArgs());
        T value = expression.getValue(ec, returnType);
        return value;
    }

    /**
     * 获取方法上的指定注解
     * @param pjp
     * @param clzz
     * @return
     * @param <T>
     */
    private <T extends Annotation> T getMethodAnnotation(ProceedingJoinPoint pjp, Class<T> clzz) { //<T>用于声明一个泛型类型参数
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        T annotation = method.getDeclaredAnnotation(clzz);
        return annotation;
    }

    private Type getMethodReturnType(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Type returnType = signature.getMethod().getGenericReturnType();
        return returnType;
    }
}
