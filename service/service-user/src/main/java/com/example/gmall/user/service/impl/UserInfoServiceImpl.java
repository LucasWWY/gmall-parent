package com.example.gmall.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.gmall.common.constant.RedisConst;
import com.example.gmall.common.execption.GmallException;
import com.example.gmall.common.result.ResultCodeEnum;
import com.example.gmall.common.util.MD5;
import com.example.gmall.user.entity.UserInfo;
import com.example.gmall.user.mapper.UserInfoMapper;
import com.example.gmall.user.service.UserInfoService;
import com.example.gmall.user.vo.LoginSuccessVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
* @author LucasWWY
* @description
* @createDate 2024-05-14 22:58:55
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService{

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public LoginSuccessVO login(UserInfo userInfo) {
        LoginSuccessVO loginSuccessVO = new LoginSuccessVO();

        //响应结果以json方式交给前端，他那边处理了（1）放大域名  （2）有效时间
        String loginName = userInfo.getLoginName();
        String passwd = MD5.encrypt(userInfo.getPasswd());

        //去数据库按照name & pwd找到用户
        UserInfo entity = lambdaQuery().
                eq(UserInfo::getLoginName, loginName).
                eq(UserInfo::getPasswd, passwd)
                .one();

        ////任何业务不预期的行为直接抛异常，由全局异常处理 (之前写的AOP？) 来进行捕获统一返回
        if (entity == null) {
            throw new GmallException(ResultCodeEnum.LOGIN_ERROR);
        }

        String token = UUID.randomUUID().toString().replace("-", ""); //也可以用JWT（加密）
        loginSuccessVO.setToken(token);
        loginSuccessVO.setUserId(userInfo.getId());
        loginSuccessVO.setNickName(userInfo.getNickName());

        //服务端通过redis共享session数据
        stringRedisTemplate.opsForValue().set(RedisConst.LOGIN_USER + token, JSON.toJSONString(entity), 7, TimeUnit.DAYS);
        //redis实现的session中有token，说明登陆了

        return loginSuccessVO;
    }

    @Override
    public void logout(String token) {
        stringRedisTemplate.delete(RedisConst.LOGIN_USER + token); //JWT体系：request header携带token
    }
}




