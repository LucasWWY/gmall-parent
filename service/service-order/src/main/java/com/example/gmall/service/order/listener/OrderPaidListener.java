package com.example.gmall.service.order.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.gmall.common.constant.MqConst;
import com.example.gmall.common.mq.MqService;
import com.example.gmall.model.mq.ware.WareStockMsg;
import com.example.gmall.model.order.entity.OrderDetail;
import com.example.gmall.model.order.entity.OrderInfo;
import com.example.gmall.model.order.entity.PaymentInfo;
import com.example.gmall.service.order.biz.OrderBizService;
import com.example.gmall.service.order.service.OrderDetailService;
import com.example.gmall.service.order.service.OrderInfoService;
import com.example.gmall.service.order.service.PaymentInfoService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lfy
 * @Description
 * @create 2022-12-24 15:22
 */
@Slf4j
@Service
public class OrderPaidListener {


    @Autowired
    MqService mqService;

    @Autowired
    OrderBizService orderBizService;

    @Autowired
    PaymentInfoService paymentInfoService;

    @Autowired
    OrderInfoService orderInfoService;

    @Autowired
    OrderDetailService orderDetailService;

    /**
     * 监听所有成功支付单
     */
    @RabbitListener(queues = MqConst.ORDER_PAYED_QUEUE)
    public void listen(Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        String json = new String(message.getBody());
        try {
            //支付宝服务器返回的json 转化成 map类型的content
            Map<String, String> content = JSON.parseObject(json, new TypeReference<Map<String, String>>() {
            });
            //1. 拿数据知道修改哪一个order
                //1.1 订单的唯一对外交易号
            String out_trade_no = content.get("out_trade_no"); //example-1671864511180-3
            String[] split = out_trade_no.split("-");
                //1.2 用户id
            Long userId =  Long.parseLong(split[split.length-1]);

            //2. 根据唯一对外交易号 和 用户id（分片键）修改订单为已支付状态
            orderBizService.payedOrder(out_trade_no,userId);

            //3. 保存此次支付回调的数据信息 payment_info 也就是 商家保存支付宝服务器返回支付信息
            PaymentInfo info = preparePaymentInfo(json, content, out_trade_no, userId);
            paymentInfoService.save(info); //涉及到外部服务调用，@Transactional不适用，只管理本地数据操作，也就是只设计order库
            //这里使用了MQ，其可持久特性帮助我们实现了类似分布式事务的功能

            //4. 怎么实现支付后锁定库存？
            //解决：发送消息通知库存系统扣减库存
            WareStockMsg msg = prepareWareStockMsg(out_trade_no,userId);
            mqService.send(msg,MqConst.WARE_STOCK_EXCHANGE,MqConst.WARE_STOCK_RK);
            channel.basicAck(tag,false);
        }catch (Exception e){
            mqService.retry(channel,tag,json,5);
        }
    }

    private PaymentInfo preparePaymentInfo(String json, Map<String, String> content, String out_trade_no, Long userId) {
        OrderInfo orderInfo = orderInfoService.lambdaQuery()
                .eq(OrderInfo::getUserId, userId)
                .eq(OrderInfo::getOutTradeNo, out_trade_no)
                .one();
        PaymentInfo info = new PaymentInfo();
        info.setUserId(userId);
        info.setOutTradeNo(out_trade_no);
        info.setOrderId(orderInfo.getId());
        info.setPaymentType(orderInfo.getPaymentWay());
        String trade_no = content.get("trade_no"); //支付宝的流水号 与我们自己的out_trade_no区分开来
        info.setTradeNo(trade_no);
        String total_amount = content.get("total_amount");
        info.setTotalAmount(new BigDecimal(total_amount));
        info.setSubject(content.get("subject"));
        info.setPaymentStatus(content.get("trade_status"));
        info.setCreateTime(new Date());
        info.setCallbackTime(new Date());
        info.setCallbackContent(json);
        return info;
    }

    private WareStockMsg prepareWareStockMsg(String out_trade_no, Long userId) {
        OrderInfo info = orderInfoService.lambdaQuery()
                .eq(OrderInfo::getOutTradeNo, out_trade_no)
                .eq(OrderInfo::getUserId, userId)
                .one();
        //1、订单基本信息
        WareStockMsg msg = new WareStockMsg();
        msg.setOrderId(info.getId());
        msg.setUserId(info.getUserId());
        msg.setConsignee(info.getConsignee());
        msg.setConsigneeTel(info.getConsigneeTel());
        msg.setOrderComment(info.getOrderComment());
        msg.setOrderBody(info.getTradeBody());
        msg.setDeliveryAddress(info.getDeliveryAddress());
        msg.setPaymentWay("2");


        //2、订单的明细（购买了哪些商品）
        List<OrderDetail> details = orderDetailService.lambdaQuery()
                .eq(OrderDetail::getOrderId, info.getId())
                .eq(OrderDetail::getUserId, info.getUserId())
                .list();

        //List<Sku>
        List<WareStockMsg.Sku> skus = details.stream().map(item -> {
            WareStockMsg.Sku sku = new WareStockMsg.Sku();
            sku.setSkuId(item.getSkuId());
            sku.setSkuNum(item.getSkuNum());
            sku.setSkuName(item.getSkuName());
            return sku;
        }).collect(Collectors.toList());
        msg.setDetails(skus);

        return msg;
    }
}
