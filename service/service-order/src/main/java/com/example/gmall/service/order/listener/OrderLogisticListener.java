package com.example.gmall.service.order.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.gmall.common.config.mq.MqService;
import com.example.gmall.common.constant.MqConst;
import com.example.gmall.mq.logistic.OrderLogisticMsg;
import com.example.gmall.service.order.biz.LogisticService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lfy
 * @Description
 * @create 2022-12-26 14:17
 */
@Slf4j
@Service
public class OrderLogisticListener {

    @Autowired
    LogisticService logisticService;

    @Autowired
    MqService mqService;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = MqConst.ORDER_LOGISTIC_QUEUE,durable = "true",autoDelete = "false",exclusive = "false"),
                    exchange = @Exchange(value = MqConst.ORDER_EVENT_EXCHANGE,
                            durable = "true",autoDelete = "false",type = "topic"),
                    key = MqConst.ORDER_LOGISTIC_RK
            )
    })
    public void listen(Message message, Channel channel) throws Exception {
        long tag = message.getMessageProperties().getDeliveryTag();
        String content = new String(message.getBody());
        try {
            OrderLogisticMsg msg = JSON.parseObject(content, OrderLogisticMsg.class);
            log.info("订单：{}。正在准备电子面单",msg.getOrderId());


            //生成电子面单
            JSONObject jsonObject = logisticService.generateEOrder(msg.getOrderId(),msg.getUserId());

            //修改订单内容；给订单添上物流号，并标记为已发货
            log.info("电子面单数据：{}",jsonObject);
            channel.basicAck(tag,false);
        }catch (Exception e){
            mqService.retry(channel,tag,content,5);
        }



    }
}
