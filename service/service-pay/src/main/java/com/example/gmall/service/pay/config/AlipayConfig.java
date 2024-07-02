package com.example.gmall.service.pay.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.example.gmall.service.pay.config.properties.AlipayProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lfy
 * @Description
 * @create 2022-12-24 11:25
 */
@EnableConfigurationProperties(AlipayProperties.class)
@Configuration
public class AlipayConfig {

    @Bean
    AlipayClient alipayClient(AlipayProperties alipayProperties){
        return new DefaultAlipayClient(alipayProperties.getGatewayUrl(),
                alipayProperties.getApp_id(),
                alipayProperties.getMerchant_private_key(),
                "json",
                alipayProperties.getCharset(),
                alipayProperties.getAlipay_public_key(),
                alipayProperties.getSign_type());
    }


}
