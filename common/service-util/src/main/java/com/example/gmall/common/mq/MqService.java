package com.example.gmall.common.mq;

import com.alibaba.fastjson.JSON;
import com.example.gmall.common.util.MD5;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 28/6/2024 - 9:47 pm
 * @Description
 */
@Slf4j
@Service
public class MqService {

    private RabbitTemplate rabbitTemplate;
    private StringRedisTemplate redisTemplate;

    //如果一个类只有有参构造器，Spring 会尝试从 IOC 容器中获取所需的依赖并通过构造器注入这些依赖
    //所以没加@Autowired也可以
    public MqService(RabbitTemplate rabbitTemplate, StringRedisTemplate redisTemplate){
        this.rabbitTemplate = rabbitTemplate;
        this.redisTemplate = redisTemplate;
        initTemplate();
    }

    private void initTemplate() {
        //confirm回调
        /**
         * 成功： ack=true - rabbitTemplate.convertAndSend("hello","a","哈哈哈哈");
         * 失败：
         *  交换机不存在: ack=false - rabbitTemplate.convertAndSend("??","a","哈哈哈哈");
         *  队列不存在：  ack=true - rabbitTemplate.convertAndSend("hello","ab","哈哈哈哈")
         * 因为confirm回调来自exchange，所以队列不存在但交换机存在的情况仍然是true
         * correlationData： 关联的数据
         * ack：  回复状态
         * cause: 原因
         */
        rabbitTemplate.setConfirmCallback((CorrelationData correlationData,
                                           boolean ack,
                                           String cause)->{
            log.info("confirm回调： data:{},ack:{},cause:{}",correlationData,ack,cause);
            //发不成功的消息要记录到后台数据库，等待人工处理
        });

        //return回调
        /**
         * 成功：不触发回调
         * 失败：
         *   队列不存在： replyCode：312 提示错误
         * message: 当前消息
         * replyCode: 回复码
         * replyText：回复文本
         * exchange: 交换机
         * routingKey：路由键
         */
        rabbitTemplate.setReturnCallback((Message message,
                                          int replyCode,
                                          String replyText,
                                          String exchange,
                                          String routingKey)->{
            log.info("return回调： message:{},replyCode:{},replyText:{},exchange:{},routingKey:{}", message,replyCode,replyText,exchange,routingKey);
            //TODO 发不成功的消息要记录到后台数据库，等待人工处理
        });

        //设置重试模板
        rabbitTemplate.setRetryTemplate(new RetryTemplate()); //不再有回调逻辑
    }

    /**
     * 发消息
     * @param message
     * @param exchange
     * @param routingKey
     */
    public void send(Object message, String exchange, String routingKey){
        rabbitTemplate.convertAndSend(exchange, routingKey, JSON.toJSONString(message));
    }

    /**
     * 有限次数重试
     * @param channel
     * @param tag
     * @param content
     * @param retryCount
     * @throws IOException
     */
    public void retry(Channel channel, long tag, String content, Integer retryCount) throws IOException {
        String md5 = MD5.encrypt(content);
        //2、同一个消息最多重试5次
        Long increment = redisTemplate.opsForValue().increment("msg:count:" + md5);
        if(increment <= retryCount){
            log.info("消费失败；重新入队；次数：{}",increment);  //导致无限入队消费，cpu打满
            channel.basicNack(tag,false,true);
        }else {
            log.error("消费重试超过最大限定次数：已达到{}，记录到数据库，不在重试，等待人工处理",increment);
            redisTemplate.delete("msg:count:" + md5);
            channel.basicAck(tag,false);
        }
    };
}
