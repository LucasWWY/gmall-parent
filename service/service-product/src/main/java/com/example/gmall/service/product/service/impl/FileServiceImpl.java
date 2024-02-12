package com.example.gmall.service.product.service.impl;

import com.example.gmall.common.properties.MinioProperties;
import com.example.gmall.common.util.DateUtil;
import com.example.gmall.service.product.service.FileService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 11/2/2024 - 6:25 pm
 * @Description
 */
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    MinioProperties minioProperties;

    @Autowired
    MinioClient minioClient;

    @Override
    public String upload(MultipartFile file) throws Exception {
        //Java Client API，但是在SpringBoot中，我们要交给Spring容器来管理对象，所以用MinioConfig解决
        //1、创建MinioClient
        //MinioClient minioClient =
        //        MinioClient.builder()
        //                .endpoint("https://play.min.io")
        //                .credentials("Q3AM3UQ867SPQQA43P2F", "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG")
        //                .build();

        //2、判断bucket是否存在
        //boolean exists = client.bucketExists(BucketExistsArgs.builder().bucket(minioProperties.getBucketName()).build());
        //if (!exists) {
        //    client.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.getBucketName()).build());
        //    System.out.println("bucket创建成功");
        //}

        //3、上传文件，加上唯一前缀
        String date = DateUtil.formatDate(new Date());
        String filename =  date + "/" + UUID.randomUUID().toString() + "_"+ file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();

        minioClient.putObject(
                PutObjectArgs.builder().bucket("gmall-oss").object("filename")
                        .stream(inputStream, file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build());

        //4、返回文件的访问地址
        String url = minioProperties.getEndpoint()+"/"+minioProperties.getBucketName()+"/"+filename;

        return url;
    }

}
