package com.example.gmall.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.gmall.model.user.entity.UserInfo;
import com.example.gmall.model.user.vo.LoginSuccessVO;

/**
* @author wangweiyedemacbook
* @description 针对表【user_info(用户表)】的数据库操作Service
* @createDate 2024-05-14 22:58:55
*/
public interface UserInfoService extends IService<UserInfo> {

    LoginSuccessVO login(UserInfo userInfo);

    void logout(String token);
}
