package com.example.gmall.model.mq.seckill;

import lombok.Data;

/**
 * @author lfy
 * @Description
 * @create 2022-12-27 11:42
 */
@Data
public class SeckillOrderMsg {
    private Long userId;
    private String code;
    private Long skuId;
    private String date;
}
