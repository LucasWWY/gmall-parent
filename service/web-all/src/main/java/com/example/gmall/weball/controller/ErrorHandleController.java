//package com.example.gmall.weball.controller;
//
//import com.example.gmall.common.result.Result;
//import com.example.gmall.common.result.ResultCodeEnum;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletResponse;
//
///**
// * @author Lucas (Weiye) Wang
// * @version 1.0.0
// * @date 3/4/2024 - 9:58 am
// * @Description
// */
//
//@RestController
//public class ErrorHandleController {
//
//    @GetMapping("/div")
//    public Result div_v1(@RequestParam("num") Long num, HttpServletResponse response){
//        //还需要考虑异常情况，并且try catch，太麻烦
//        try {
//            long i = 10/num;
//            return Result.ok(i);
//        } catch (Exception e) {
//            Result<String> result = Result.build("", ResultCodeEnum.FAIL);
//            return result;
//        }
//    }
//
//    @GetMapping("/div")
//    public Result div_v2(@RequestParam("num") Long num, HttpServletResponse response){
//
////        Cookie cookie = new Cookie("jsessionidxxx","123");
////        cookie.setDomain(".jd.com"); //直接发令牌的时候放大
////        response.addCoxokie(cookie);
//
//        //有了ExceptionHandler 只写正常逻辑
//        long i = 10/num;
//        return Result.ok(i);
//    }
//
//    /**
//     * 处理当前controller的所有异常，进一步引入一个GlobalExceptionHandler处理所有controller异常
//     * @return
//     */
//    @ExceptionHandler({Exception.class})
//    public Result handleException(Exception e){
//        Result<Object> fail = Result.fail();
//        fail.setMessage(e.getMessage());
//        return fail;
//    }
//}
