package com.example.gmall.common.config.thread.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 19/2/2024 - 6:32 pm
 * @Description
 */

@Data
@ConfigurationProperties(prefix = "gmall.threadpool")
public class MyThreadPoolProperties {

    private Integer corePoolSize = 4;

    private Integer maximumPoolSize = 8;

    private Long keepAliveTime = 5L;

    private Integer workQueueSize = 1000;
}
