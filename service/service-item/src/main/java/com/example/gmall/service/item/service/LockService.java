package com.example.gmall.service.item.service;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 26/2/2024 - 3:39 pm
 * @Description
 */
public interface LockService {

    String lock();

    void unlock(String uuid);
}
