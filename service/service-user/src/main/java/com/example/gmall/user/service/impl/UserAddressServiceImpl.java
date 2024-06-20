package com.example.gmall.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import generator.entity.UserAddress;
import com.example.gmall.user.service.UserAddressService;
import generator.mapper.UserAddressMapper;
import org.springframework.stereotype.Service;

/**
* @author wangweiyedemacbook
* @description 针对表【user_address(用户地址表)】的数据库操作Service实现
* @createDate 2024-05-14 22:58:55
*/
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress>
    implements UserAddressService{

}




