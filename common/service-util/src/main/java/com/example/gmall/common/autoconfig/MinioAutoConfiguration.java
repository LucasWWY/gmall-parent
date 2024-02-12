package com.example.gmall.common.autoconfig;

import com.example.gmall.common.config.MinioConfig;
import com.example.gmall.common.properties.MinioProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 13/2/2024 - 2:21 am
 * @Description
 */
//1、解决：MinioProperties注册到容器中
//2、@ConfigurationProperties只为组件绑定属性，一旦注册属性绑定立刻生效
//但是可以用其他方式注册组件，比如类上加@Component，方法上加@Bean
@Import({MinioConfig.class})
@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class MinioAutoConfiguration {
}
