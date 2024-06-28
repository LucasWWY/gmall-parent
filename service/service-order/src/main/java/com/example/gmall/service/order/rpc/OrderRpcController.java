package com.example.gmall.service.order.rpc;

import com.example.gmall.common.authen.UserAuthUtils;
import com.example.gmall.common.result.Result;
import com.example.gmall.service.order.biz.OrderBizService;
import com.example.gmall.service.order.service.OrderInfoService;
import com.example.gmall.model.order.entity.OrderInfo;
import com.example.gmall.model.order.vo.OrderConfirmRespVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author lfy
 * @Description
 * @create 2022-12-21 14:12
 */
@RequestMapping("/api/inner/rpc/order")
@RestController
public class OrderRpcController {

    @Autowired
    OrderBizService orderBizService;

    @Autowired
    OrderInfoService orderInfoService;

    /**
     * 获取订单确认页数据
     * @return
     */
    @GetMapping("/confirmdata")
    public Result<OrderConfirmRespVO> orderConfirmData(){

        OrderConfirmRespVO respVo = orderBizService.getOrderConfirmData();

        return Result.ok(respVo);
    }

    /**
     * 根据订单id查询订单
     * @param id
     * @return
     */
    @GetMapping("/info/{id}")
    public Result<OrderInfo> getOrderInfoById(@PathVariable("id") Long id){

        Long userId = UserAuthUtils.getUserId();;
        OrderInfo orderInfo = orderInfoService.getOrderInfoByIdAndUserId(id, userId);
        //为什么传userId？因为分片键是userId,orderId需要遍历所有数据

        return Result.ok(orderInfo);
    }


    /**
     * 保存秒杀单
     * @param info
     * @return
     */
    @PostMapping("/seckill/order")
    public Result<Long> saveSeckillOrder(@RequestBody OrderInfo info){

        Long orderId =  orderBizService.saveSeckillOrder(info);
        return Result.ok(orderId);
    }
}
