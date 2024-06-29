package com.example.gmall.service.order.listener;

import com.alibaba.fastjson.JSON;
import com.example.gmall.common.constant.MqConst;
import com.example.gmall.common.mq.MqService;
import com.example.gmall.model.order.entity.OrderInfo;
import com.example.gmall.service.order.biz.OrderBizService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class OrderCloseListener {


    @Autowired
    OrderBizService orderBizService;

    @Autowired
    MqService mqService;

    //**消费监听器模版**
    //mqService.send(orderInfo, MqConst.ORDER_EVENT_EXCHANGE,MqConst.ORDER_CREATE_RK);
    //消费者开启了消费端手动ack（配置文件在nacos上），必须完成后给服务器回复确认，服务器才删除消息
    //@RabbitListener(queues = "haha")  //说明要监听消息；使用前提：开启基于注解的rabbit功能
    //public void listen(Message message, Channel channel) throws IOException {
    //
    //    long tag = message.getMessageProperties().getDeliveryTag(); //消息标签：标识唯一性
    //    try {
    //        String content = new String(message.getBody());
    //        System.out.println("收到消息："+content + "；  正在处理....");
    //        //业务处理
    //
    //        //回复ok
    //        channel.basicAck(tag,false); //2nd param: 批量处理
    //        System.out.println(tag +" 完成后，回复ok");
    //    }catch (Exception e){
    //        //出现异常回复失败
    //        channel.basicNack(tag,false,true); //3nd: 重新入队
    //    }
    //
    //}

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

            OrderInfo orderInfo = JSON.parseObject(content, OrderInfo.class);
            //进行关单
            //如果消息重复了怎么办？ 关单会不会重复？要保证收消息的业务操作是幂等操作
            //只有unpaid状态的order才是需要关闭的，检测逻辑封装在了closeOrder
            orderBizService.closeOrder(orderInfo.getId(), orderInfo.getUserId());

            channel.basicAck(tag,false);
        }catch (Exception e){
            //1. 死循环方案
            //channel.basicNack(tag,false,true); //3nd: 确认失败则重新入队，但会出现死循环

            //2、只要消息的MD5相同就是同一个消息，同一个消息最多重试5次
            //Long increment = redisTemplate.opsForValue().increment("msg:count:" + MD5.encrypt(content));
            //if (increment > 5) {
            //    redisTemplate.delete("msg:count:" + MD5.encrypt(content));
            //    channel.basicAck(tag,false);
            //} else {
            //    channel.basicNack(tag,false,true); //3nd: 确认失败则重新入队
            //}

            //3. 把2的逻辑封装并且产出log
            mqService.retry(channel, tag, content,5); //为防止死循环
        }

    }


}
