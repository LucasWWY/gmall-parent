package com.example.gmall.service.item.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

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

    //最模糊：@Before("execution(public * *.*(..))")
    //最完整：@Before("execution(public * com.example.gmall.service.item.service.SkuDetailService.getSkuDetailData(java.lang.Long))")
    //*：非参数位置，匹配任意
    //..：任意参数
    //public void cut(){};


    //可以复用的切入点表达式
    @Pointcut("execution(public * com.example.gmall.service.item.service.SkuDetailService.getSkuDetailData(..))")
    public void pc(){};

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


    //用环绕通知拦截目标方法的执行
    //ProceedingJoinPoint 是 JoinPoint 的扩展，专门用于那些环绕通知（around advice）中。它不仅包含了 JoinPoint 的所有功能，还添加了 proceed() 方法。这个方法非常关键，它允许环绕通知执行被通知的方法。
    //连接点代表了程序执行过程中的某个特定点，比如方法的调用或异常的抛出。简而言之，它是你可以插入一个方面（aspect）的执行点。
    //@Around(value = "pc()")
    @Around(value = "pc()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        log.info("缓存环绕切面介入...");
        Object retVal = null;
        try {
            //1. 前置通知
            //放置 Redisson分布式锁 + 分布式缓存Redis

            //以前自己实现的动态代理，利用反射执行目标方法
            retVal = pjp.proceed(); //推进目标方法getSkuDetailData(Long skuId)的执行

            //2. 返回通知

        } catch (Exception e) {
            //3. 异常通知
            throw e; //保证controller 或 调用者依旧能感知异常
        } finally {
            //4. 后置通知
        }

        return retVal;
    }
}
