package com.example.gmall.common.exception;

import com.example.gmall.common.execption.GmallException;
import com.example.gmall.common.result.Result;
import com.example.gmall.common.result.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 3/4/2024 - 10:31 am
 * @Description
 */
//@ResponseBody //返回给前端需要是JSON，不用再在每个方法上加@ResponseBody
//@ControllerAdvice //aop：切面编程，所有controller的异常处理切面
@RestControllerAdvice //@ResponseBody + @ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseBody //返回给前端需要是JSON，之前在ErrorHandleController为什么没标？因为其被标注了@RestController，包含了@ResponseBody
    @ExceptionHandler({NullPointerException.class})
    public Result handleNullPointerException(Exception e){
        Result<Object> fail = Result.fail();
        fail.setMessage(e.getMessage());
        return fail;
    }

    //和业务有关的异常
    @ResponseBody //返回给前端需要是JSON，之前在ErrorHandleController为什么没标？因为其被标注了@RestController，包含了@ResponseBody
    @ExceptionHandler({GmallException.class})
    public Result handleGmallException(GmallException e){
        Result<Object> fail = Result.fail();
        fail.setCode(e.getCode());
        fail.setMessage(e.getMessage());
        return fail;
    }

    //处理所有controller出现的其他异常
    @ResponseBody //返回给前端需要是JSON，之前在ErrorHandleController为什么没标？因为其被标注了@RestController，包含了@ResponseBody
    @ExceptionHandler({Exception.class})
    public Result handleException(Exception e){
        log.error("全局异常处理：", e);
        Result<Object> fail = Result.fail();
        fail.setMessage(e.getMessage());
        return fail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException exception){

        //1、从这个异常中拿到校验结果
        BindingResult bindingResult = exception.getBindingResult();
        //2、把结果整理下返回前端：  {tel:"",consignee:""}
        Map<String,String> errMap = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            String field = error.getField(); //错误发生在哪个属性
            String message = error.getDefaultMessage(); //错误消息
            errMap.put(field,message);
        }
        return Result.build(errMap, ResultCodeEnum.INVAILD_PARAM);
    }
}
