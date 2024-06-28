package com.example.gmall.weball.controller;

import com.example.gmall.feign.order.OrderFeignClient;
import com.example.gmall.model.order.entity.OrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author lfy
 * @Description
 * @create 2022-12-23 11:13
 */
@Controller
public class PayController {


    @Autowired
    OrderFeignClient orderFeignClient;

    @GetMapping("/pay.html")
    public String payPage(@RequestParam("orderId") Long orderId,
                          Model model){

        //远程调用订单，把订单数据查出来
        OrderInfo orderInfo = orderFeignClient.getOrderInfoById(orderId).getData();
        model.addAttribute("orderInfo", orderInfo);
        return "payment/pay";
    }


    /**
     * 支付成功提示页
     * @return
     */
    @GetMapping("/pay/success.html")
    public String paySuccess(){
        //订单状态，远程调用改为已支付？
        //绝对不能在这个页面被访问的时候修改订单状态为已支付
        return "payment/success";
    }
}
