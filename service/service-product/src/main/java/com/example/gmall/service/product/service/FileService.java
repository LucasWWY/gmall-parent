package com.example.gmall.service.product.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 11/2/2024 - 6:25 pm
 * @Description
 */
public interface FileService {

    /**
     * 把文件上传到Minio
     * @param file
     * @return
     */
    String upload(MultipartFile file) throws Exception;
}
