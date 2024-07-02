package com.example.gmall.model.mq.ware;

import lombok.Data;

/**
 * @author lfy
 * @Description
 * @create 2022-12-26 9:57
 */
@Data
public class WareStockResultMsg {
    private Long orderId;
    private String status;
}
