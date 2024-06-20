package com.example.gmall.user.controller;

import com.example.gmall.common.result.Result;
import com.example.gmall.user.entity.UserInfo;
import com.example.gmall.user.service.UserInfoService;
import com.example.gmall.user.vo.LoginSuccessVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 15/5/2024 - 5:02 am
 * @Description
 */
@RequestMapping("/api/user")
@RestController
public class LoginController {
    @Autowired
    UserInfoService userInfoService;

    /**
     * 登录
     * @param
     * @return
     */
    @PostMapping("/passport/login")
    public Result login(@RequestBody UserInfo userInfo){ //payload in JSON format (i.e. body) contains loginName and password

        LoginSuccessVO vo = userInfoService.login(userInfo);
        //远程调用购物车进行合并？ 现场等结果要做剩下的事情
        //像一些某些事件触发以后，其他人要做一些事情的，适合用消息队列完成事件通知机制
        //懒思想
        return Result.ok(vo);
    }


    /**
     * 退出
     * 后端删除用户在redis中的登录信息
     * @return
     */
    @GetMapping("/passport/logout")
    public Result logout(@RequestHeader("token") String token){
        userInfoService.logout(token);
        return Result.ok();
    }
}

