package com.example.gmall.common.retryer;

import feign.RetryableException;
import feign.Retryer;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 5/4/2024 - 8:30 pm
 * @Description
 * 幂等性：
 * 一个请求发一次和发2^N多次得到的结果是一样的
 * 1)、查询：幂等的
 * 2)、修改：
 * update sku_info set price=888 where id=1; 幂等的
 * update sku_info set price+=888 where id=1; 非幂等
 * 3)、删除：
 * delete from sku_info where id=20; 幂等的
 * 4)、新增：
 * insert into sku_image (aa, bb) values (1,1);非幂等
 */
public class NeverRetryer implements Retryer {

    @Override
    public void continueOrPropagate(RetryableException e) {
        throw e;
    }

    @Override
    public Retryer clone() {
        return null;
    }
}
