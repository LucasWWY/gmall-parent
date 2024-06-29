package com.example.gmall.service.order.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

//这是一个listener示范，实际业务看OrderCloseListener
@Slf4j
@Service
public class MqListener {

    //mqService.send(orderInfo, MqConst.ORDER_EVENT_EXCHANGE,MqConst.ORDER_CREATE_RK);
    //消费者开启了消费端手动ack（配置文件在nacos上），必须完成后给服务器回复确认，服务器才删除消息
    @RabbitListener(queues = "haha")  //说明要监听消息；使用前提：开启基于注解的rabbit功能
    public void listen(Message message, Channel channel) throws IOException {

        long tag = message.getMessageProperties().getDeliveryTag(); //消息标签：标识唯一性
        try {
            String content = new String(message.getBody());
            System.out.println("收到消息："+content + "；  正在处理....");
            //业务处理

            //回复ok
            channel.basicAck(tag,false); //2nd param: 批量处理
            System.out.println(tag +" 完成后，回复ok");
        }catch (Exception e){
            //出现异常回复失败
            channel.basicNack(tag,false,true); //3nd: 重新入队
        }

    }
}
