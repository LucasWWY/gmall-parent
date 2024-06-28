package com.example.gmall.feign.user;

import com.example.gmall.common.result.Result;
import com.example.gmall.model.user.entity.UserAddress;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 27/6/2024 - 1:19 am
 * @Description
 */
@RequestMapping("/api/inner/rpc/user")
@FeignClient("service-user")
public interface UserFeignClient {

    /**
     * 返回用户收货地址列表
     * @param userId
     * @return
     */
    @GetMapping("/addresses/{userId}")
    Result<List<UserAddress>> getUserAddress(@PathVariable("userId") Long userId);
}
