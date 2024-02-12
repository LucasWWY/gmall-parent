package com.example.gmall.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 12/2/2024 - 10:48 pm
 * @Description
 */
@Data
@ConfigurationProperties(prefix = "app.minio") //读取配置文件中app.minio下的所有值和JavaBean属性进行绑定
public class MinioProperties {

        String endpoint;

        String accessKey;

        String secretKey;

        String bucketName;
}
