package com.example.gmall.common.config;

import com.example.gmall.common.properties.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 12/2/2024 - 10:48 pm
 * @Description
 */
@Configuration
public class MinioConfig {

    @Autowired
    private MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() throws  Exception {
        //1、创建MinioClient
        MinioClient client = MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();

        //2、判断是否有指定的bucket
        boolean exists = client.bucketExists(BucketExistsArgs.builder().bucket(minioProperties.getBucketName()).build());
        if (!exists) {
            client.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.getBucketName()).build());
            System.out.println("bucket创建成功");
        }

        return client;
    }
}
