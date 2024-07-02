package com.example.gmall.service.pay.paynotify;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.example.gmall.common.constant.MqConst;
import com.example.gmall.common.mq.MqService;
import com.example.gmall.service.pay.config.properties.AlipayProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

//支付完成后，支付宝需要异步发送post请求把支付结果作为参数告知商家，所以要给支付宝一个通知接口
@Slf4j
@RequestMapping("/api/payment/notify")
@RestController
public class PaySuccessNotifyController {

    @Autowired
    AlipayProperties alipayProperties;

    @Autowired
    MqService mqService;

    /**
     * 支付完成以后支付宝会给我们这里通知支付结果，把url填在properties中，进而被AlipayClient使用，通过调用通知我们支付结果
     * @param params
     * @return
     */
    @PostMapping("/success") //这个url就是被内网穿透软件转化成外网地址的url
    public String paySuccessNotify(@RequestParam Map<String,String> params) throws AlipayApiException {
        log.info("收到支付宝支付消息通知:{}", JSON.toJSONString(params));

        //验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(params,
                alipayProperties.getAlipay_public_key(),
                alipayProperties.getCharset(),
                alipayProperties.getSign_type()); //调用SDK验证签名

        //推荐params中的数据页验证

        //在这里修改 而不是在PayController支付完成后跳转页面那里修改
        if(signVerified){
            //修改订单状态； 通过消息传递机制
            //为什么不直接远程调用？因为有可能服务器宕机，网络不稳定，状态更新不确定
            log.info("验签通过，准备修改订单状态");
            String trade_status = params.get("trade_status");
            if("TRADE_SUCCESS".equals(trade_status)){
                mqService.send(params, MqConst.ORDER_EVENT_EXCHANGE, MqConst.ORDER_PAID_RK);
                //通过MQ更改订单状态，虽然这里的X和提交orderBizServiceImpl的submitOrder一样
                //但是RK不一样，意味着Q不一样，那么监听器自然也不一样，后续行为就不一样
            }
        }
        //什么时候给支付宝返回success
        return "success";
    }
}
