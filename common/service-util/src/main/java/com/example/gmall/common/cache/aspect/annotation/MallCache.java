//package com.example.gmall.common.cache.aspect.annotation;
//
//import java.lang.annotation.*;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author Lucas (Weiye) Wang
// * @version 1.0.0
// * @date 11/3/2024 - 4:27 pm
// * @Description
// */
//@Target({ElementType.METHOD})
//@Retention(RetentionPolicy.RUNTIME)
//@Inherited
//@Documented
//public @interface MallCache {
//
//    /**
//     * 不同业务，缓存key不同
//     * @return
//     */
//    String cacheKey() default "";
//
//    /**
//     * 不同业务，位图不同
//     * 默认值代表不使用位图
//     * @return
//     */
//    String bitMapName() default "";
//
//    /**
//     * bitMap的key
//     * @return
//     */
//    String bitMapIndex() default "";
//
//    String lockKey() default "";
//
//    long ttl() default 30;
//
//    TimeUnit unit() default TimeUnit.SECONDS;
//}
