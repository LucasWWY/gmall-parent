//package com.example.gmall.weball;
//
//import com.example.gmall.common.execption.GmallException;
//import com.example.gmall.common.result.Result;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
///**
// * @author Lucas (Weiye) Wang
// * @version 1.0.0
// * @date 3/4/2024 - 10:31 am
// * @Description
// */
////@ResponseBody //返回给前端需要是JSON，不用再在每个方法上加@ResponseBody
////@ControllerAdvice //aop：切面编程，所有controller的异常处理切面
//@RestControllerAdvice //@ResponseBody + @ControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ResponseBody //返回给前端需要是JSON，之前在ErrorHandleController为什么没标？因为其被标注了@RestController，包含了@ResponseBody
//    @ExceptionHandler({NullPointerException.class})
//    public Result handleNullPointerException(Exception e){
//        Result<Object> fail = Result.fail();
//        fail.setMessage(e.getMessage());
//        return fail;
//    }
//
//    //和业务有关的异常
//    @ResponseBody //返回给前端需要是JSON，之前在ErrorHandleController为什么没标？因为其被标注了@RestController，包含了@ResponseBody
//    @ExceptionHandler({GmallException.class})
//    public Result handleGmallException(GmallException e){
//        Result<Object> fail = Result.fail();
//        fail.setCode(e.getCode());
//        fail.setMessage(e.getMessage());
//        return fail;
//    }
//}
