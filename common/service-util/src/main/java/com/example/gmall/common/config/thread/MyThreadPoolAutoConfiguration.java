package com.example.gmall.common.config.thread;

import com.example.gmall.common.config.thread.properties.MyThreadPoolProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 19/2/2024 - 5:52 pm
 * @Description
 */
@EnableConfigurationProperties(MyThreadPoolProperties.class)
@Configuration
public class MyThreadPoolAutoConfiguration {

    /**
     * 自定义线程池，因为默认可能出现内存泄漏的情况 i.e. 线程的阻塞队列可能会无限增长
     * int corePoolSize, 核心线程数，一直存在
     * int maximumPoolSize, 弹性线程，最大线程数，阻塞队列满，且线程数小于最大线程数时，会新建线程
     * long keepAliveTime, 弹性线程的存活时间：多久不干活释放
     * TimeUnit unit, 时间单位
     * BlockingQueue<Runnable> workQueue, 阻塞队列，人物过来核心线程进行处理，核心线程都忙，则新任务进入阻塞队列
     * ThreadFactory threadFactory, 线程工厂，创建新线程
     * RejectedExecutionHandler handler 拒绝策略，核心 最大 阻塞队列都满了就需要拒绝
     * @return
     *
     * 队列大小：
     * 1）、内存：未来微服务部署到哪种机器，主要因素
     * 2）、压测：峰值 * 1.5
     */
    @Bean
    public ThreadPoolExecutor coreExecutor(MyThreadPoolProperties threadPoolProperties) {
        return new ThreadPoolExecutor(threadPoolProperties.getCorePoolSize(),
                threadPoolProperties.getMaximumPoolSize(),
                threadPoolProperties.getKeepAliveTime(),
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(threadPoolProperties.getWorkQueueSize()), //碎片化空间效率最高，array是连续空间
                new ThreadFactory() {
                int i = 1;
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName("核心线程[" + i++ + "]"); //一次性最多开24个，因为最大线程数是24
                        thread.setPriority(10); //和非核心线程池配合使用
                        return thread;
                    }
                }
        );
    }

    //@Bean
    //public ThreadPoolExecutor otherExecutor() { //非核心线程池，线程池任务隔离，做一些不重要的任务，任务down掉不影响大局
    //    return new ThreadPoolExecutor(8,
    //            24,
    //            5,
    //            TimeUnit.MINUTES,
    //            new LinkedBlockingQueue<>(3000), //阻塞队列，碎片化空间效率最高，array是连续空间
    //            new ThreadFactory() {
    //                int i = 1;
    //                @Override
    //                public Thread newThread(Runnable r) {
    //                    Thread thread = new Thread(r);
    //                    thread.setName("核心线程：" + i++); //一次性最多开24个，因为最大线程数是24
    //                    thread.setPriority(5);
    //                    return thread;
    //                }
    //            },
    //            new ThreadPoolExecutor.CallerRunsPolicy()
    //    );
    //}


}
