package com.example.gmall.user.rpc;

import com.example.gmall.common.result.Result;
import com.example.gmall.model.user.entity.UserAddress;
import com.example.gmall.user.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 27/6/2024 - 1:14 am
 * @Description
 */
@RequestMapping("/api/inner/rpc/user")
@RestController
public class UserRpcController {

    @Autowired
    UserAddressService userAddressService;

    /**
     * 返回用户收货地址列表
     * @param userId
     * @return
     */
    @GetMapping("/addresses/{userId}")
    public Result<List<UserAddress>> getUserAddress(@PathVariable("userId") Long userId){
        List<UserAddress> list = userAddressService
                .lambdaQuery()
                .eq(UserAddress::getUserId, userId)
                .list();
        return Result.ok(list);
    }
}

