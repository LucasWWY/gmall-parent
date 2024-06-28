package com.example.gmall.service.order.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author lfy
 * @Description
 * @create 2022-12-23 15:41
 */
@Slf4j
@Service
public class MqListener {



    //消费者开启了手动ack（手动确认模式），必须完全给服务器回复ok；服务器才删除消息
    @RabbitListener(queues = "haha")  //说明要监听消息
    public void listen(Message message, Channel channel) throws IOException {

        long tag = message.getMessageProperties().getDeliveryTag();

        try {
            String content = new String(message.getBody());
            System.out.println("收到消息："+content + "；  正在处理....");

            //业务处理

            //回复ok
            channel.basicAck(tag,false);
            System.out.println(tag +" 回复ok完成");
        }catch (Exception e){

            //出现异常回复失败
            channel.basicNack(tag,false,true);
        }


    }
}
