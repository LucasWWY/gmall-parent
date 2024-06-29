package com.example.gmall.common.annotation;

import com.example.gmall.common.mq.MqService;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 28/6/2024 - 9:49 pm
 * @Description
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({MqService.class})
@EnableRabbit //开启基于注解的rabbit功能 可以使用@RabbitListener
public @interface EnableMqService {
}

