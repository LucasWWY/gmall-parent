package com.example.gmall.service.order.listener;

import com.alibaba.fastjson.JSON;
import com.example.gmall.common.config.mq.MqService;
import com.example.gmall.common.constant.MqConst;
import com.example.gmall.service.order.biz.OrderBizService;
import com.example.gmall.model.order.entity.OrderInfo;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author lfy
 * @Description
 * @create 2022-12-23 16:45
 */
@Slf4j
@Service
public class OrderCloseListener {


    @Autowired
    OrderBizService orderBizService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    MqService mqService;

    /**
     * 监听死信队列中所有待关闭的订单
     * @param message
     * @param channel
     */
    @RabbitListener(queues = MqConst.ORDER_DEAD_QUEUE)
    public void listen(Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        String content = new String(message.getBody());
        try {
            log.info("收到需要关单的消息：{}",content);
            OrderInfo info = JSON.parseObject(content, OrderInfo.class);
            //进行关单
            //消息重复；  10次； 要保证收消息的业务方是幂等操作
            orderBizService.closeOrder(info.getId(),info.getUserId());

            channel.basicAck(tag,false);
        }catch (Exception e){
            //1、只要消息的MD5相同就是同一个消息
            mqService.retry(channel, tag, content,5);
        }

    }


}
