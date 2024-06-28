package com.example.gmall.weball.controller;

import com.example.gmall.feign.order.OrderFeignClient;
import com.example.gmall.model.order.vo.OrderConfirmRespVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author lfy
 * @Description
 * @create 2022-12-21 14:00
 */
@Controller
public class OrderController {

    @Autowired
    OrderFeignClient orderFeignClient;

    /**
     * 订单确认页
     * @param model
     * @return
     */
    @GetMapping("/trade.html")
    public String trade(Model model){
        //远程调用订单获取订单确认页的数据，并展示
        OrderConfirmRespVO data = orderFeignClient.orderConfirmData().getData();

        //详情列表：购买了哪些商品{skuId、imgUrl、skuName、orderPrice、skuNum}
        model.addAttribute("detailArrayList",data.getDetailArrayList());
        model.addAttribute("totalNum",data.getTotalNum());
        model.addAttribute("totalAmount",data.getTotalAmount());
        model.addAttribute("userAddressList",data.getUserAddressList());
        model.addAttribute("tradeNo",data.getTradeNo());
        return "order/trade";
    }

    //myOrder.html
    @GetMapping("/myOrder.html")
    public String orderListPage(){

        //TODO 订单列表
        return "order/myOrder";
    }
}
