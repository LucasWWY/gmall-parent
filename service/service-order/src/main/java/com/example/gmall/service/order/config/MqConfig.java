package com.example.gmall.service.order.config;

import com.example.gmall.common.constant.MqConst;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MqConfig { //自动创建 X Q binding

    @Bean
    public Exchange orderEventExchange(){

        /**
         * String name,  交换机名
         * boolean durable,  持久化
         * boolean autoDelete, 自动删除
         * Map<String, Object> arguments
         */
        return new TopicExchange(MqConst.ORDER_EVENT_EXCHANGE,true, false,null); //一对多绑定
    }

    @Bean  //延迟队列，不能让任何人监听
    public Queue orderDelayQueue(){
        /**
         * String name,  队列名
         * boolean durable, 持久化
         * boolean exclusive, 是否排他（虽然排他 但是只有1个人能接到消息）
         * boolean autoDelete, 自动删除
         * @Nullable Map<String, Object> arguments 参数设置
         */
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange",MqConst.ORDER_EVENT_EXCHANGE); //死信队列 和 延时队列 共用一个Q，这里明确了谁是死信交换机
        arguments.put("x-dead-letter-routing-key",MqConst.ORDER_TIMEOUT_RK); //这里明确了谁是死信路由键 其实也就明确了谁是死信队列，死信xx不是根据名字判断的，而是这里设置的
        arguments.put("x-message-ttl",MqConst.ORDER_TTL);


        return new Queue(MqConst.ORDER_DELAY_QUEUE,
               true,
               false,
               false,
                arguments
               );
    }

    @Bean
    public Binding delayBinding(){
        /**
         * String destination,  目的地
         * DestinationType destinationType,  目的地类型
         * String exchange,  交换机
         * String routingKey, 路由键
         * @Nullable Map<String, Object> arguments 参数
         *
         * exchange和 destinationType的destination 使用 routingKey 进行绑定
         * order-event-exchange 交换机和 队列类型的order-delay-queue队列 使用 order.create 进行绑定
         */
       return new Binding(
               MqConst.ORDER_DELAY_QUEUE,
               Binding.DestinationType.QUEUE,
               MqConst.ORDER_EVENT_EXCHANGE,
               MqConst.ORDER_CREATE_RK,
               null);
    }


    @Bean //死信队列，消费者监听
    public Queue orderDeadQueue(){
        /**
         * String name,  队列名
         * boolean durable, 持久化
         * boolean exclusive, 是否排他
         * boolean autoDelete, 自动删除
         * @Nullable Map<String, Object> arguments 参数设置
         */
        return new Queue(MqConst.ORDER_DEAD_QUEUE,true,false,false,null);
    }


    @Bean
    public Binding deadBinding(){
        /**
         * String destination,  目的地
         * DestinationType destinationType,  目的地类型
         * String exchange,  交换机
         * String routingKey, 路由键
         * @Nullable Map<String, Object> arguments 参数
         *
         * exchange 和 destinationType 的 destination 使用 routingKey 进行绑定
         */
        return new Binding(
                MqConst.ORDER_DEAD_QUEUE,
                Binding.DestinationType.QUEUE,
                MqConst.ORDER_EVENT_EXCHANGE,
                MqConst.ORDER_TIMEOUT_RK,
                null
        );
    }


    /**
     * 支付成功单队列
     * @return
     */
    @Bean
    public Queue payedQueue(){
        return new Queue(MqConst.ORDER_PAYED_QUEUE,
                true,false,false);
    }

    @Bean
    public Binding payedBinding(){
        return new Binding(
                MqConst.ORDER_PAYED_QUEUE,
                Binding.DestinationType.QUEUE,
                MqConst.ORDER_EVENT_EXCHANGE,
                MqConst.ORDER_PAYED_RK,
                null
        );
    }
}
