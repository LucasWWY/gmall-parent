//package com.example.gmall.service.item.retryer;
//
//import feign.RetryableException;
//import feign.Retryer;
//import lombok.extern.slf4j.Slf4j;
//
///**
// * @author Lucas (Weiye) Wang
// * @version 1.0.0
// * @date 5/4/2024 - 8:07 pm
// * @Description
// */
//@Slf4j
//public class Retryer3 implements Retryer {
//
//    int count = 3;
//    int num = 0;
//    /**
//     * 一旦远程超时，feign会自动调用这个方法
//     * 重试器的方法只要不抛出错误就会重试一次远程调用
//     * @param e
//     */
//    @Override
//    public void continueOrPropagate(RetryableException e) {
//        if (num++ >= count) { //重试三次
//            log.info("正在重试第{}次", num);
//            throw e;
//        }
//    }
//
//    @Override
//    public Retryer clone() {
//        return new Retryer3();
//    }
//}
