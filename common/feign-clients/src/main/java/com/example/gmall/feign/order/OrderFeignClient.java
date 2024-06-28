package com.example.gmall.feign.order;

import com.example.gmall.common.result.Result;
import com.example.gmall.model.order.entity.OrderInfo;
import com.example.gmall.model.order.vo.OrderConfirmRespVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author lfy
 * @Description
 * @create 2022-12-21 14:16
 */
@RequestMapping("/api/inner/rpc/order")
@FeignClient("service-order")
public interface OrderFeignClient {

    /**
     * 获取订单确认页数据
     * @return
     */
    @GetMapping("/confirmdata")
    Result<OrderConfirmRespVO> orderConfirmData();

    /**
     * 根据订单id查询订单
     * @param id
     * @return
     */
    @GetMapping("/info/{id}")
    Result<OrderInfo> getOrderInfoById(@PathVariable("id") Long id);


    /**
     * 保存秒杀单
     * @param info
     * @return
     */
    @PostMapping("/seckill/order")
    Result<Long> saveSecKillOrder(@RequestBody OrderInfo info);
}
