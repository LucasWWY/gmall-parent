package com.example.gmall.service.product.init;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 22/2/2024 - 1:46 am
 * @Description SpringBoot中监听器是在项目启动就运行的，所以我们可以把InitTask配置成监听器
 */
@Component //
@Slf4j
public class InitListener implements SpringApplicationRunListener {
    SpringApplication application;

    public InitListener(SpringApplication application, String[] args) {
        log.info("监听器对象创建：application: {}; arguments:", application, args); //IOC容器 + 请求参数
        this.application = application;
    }

    @Override
    public void starting() {
        SpringApplicationRunListener.super.starting();
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        SpringApplicationRunListener.super.started(context);
    }
}
