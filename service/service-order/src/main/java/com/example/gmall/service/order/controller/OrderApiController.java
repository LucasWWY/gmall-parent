package com.example.gmall.service.order.controller;

import com.example.gmall.common.result.Result;
import com.example.gmall.model.order.vo.OrderSplitReps;
import com.example.gmall.model.order.vo.OrderSubmitVO;
import com.example.gmall.service.order.biz.OrderBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author lfy
 * @Description
 * @create 2022-12-23 8:55
 */
@RequestMapping("/api/order")
@RestController
public class OrderApiController {

    @Autowired
    OrderBizService orderBizService;

    @PostMapping("/auth/submitOrder")
    public Result submitOrder(@RequestParam("tradeNo") String tradeNo,
                              @Valid @RequestBody OrderSubmitVO submitVO){ //@Valid开启OrderSubmitVO中的校验

        //雪花算法生成的大数字id，一定要以字符串的方式返回出去
        Long orderId = orderBizService.submitOrder(submitVO,tradeNo);
        return Result.ok(orderId+""); //雪花算法生成的64 bit，前端只能显示53bit数字，所以要以字符串形式返回
    }


    @PostMapping("/orderSplit")
    public List<OrderSplitReps> orderSplit(@RequestParam("orderId") Long orderId,
                                           @RequestParam("wareSkuMap") String json){

        //拆单：把一个大订单，拆分成多个子订单
        List<OrderSplitReps> splitReps =  orderBizService.orderSplit(orderId,json);

        return splitReps;
    }
}
