package com.example.gmall.weball.controller;

import com.example.gmall.feign.order.OrderFeignClient;
import com.example.gmall.model.order.entity.OrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PayController {

    @Autowired
    OrderFeignClient orderFeignClient;

    @GetMapping("/pay.html")
    public String payPage(@RequestParam("orderId") Long orderId,
                          Model model){

        //远程调用订单，把订单数据查出来
        OrderInfo orderInfo = orderFeignClient.getOrderInfoById(orderId).getData(); //用了@EnableUserAuthFeignInterceptor 隐式传参数
        model.addAttribute("orderInfo", orderInfo);
        return "payment/pay";
    }


    /**
     * 支付成功提示页
     * @return
     */
    @GetMapping("/pay/success.html")
    public String paySuccess(){
        //能不能在这把订单状态，远程调用改为已支付？
        //绝对不能在支付成功页面被访问的时候修改订单状态为已支付
        //1. 因为修改需要传参e.g. orderId，如果黑客拦截请求修改或伪造，那么就可能修改不应该修改订单的状态
        //2. 或者 在支付完成 正要准备请求跳转到 支付成功页面的时候，断网，那么支付了但是订单状态没改变
        return "payment/success";
    }
}
