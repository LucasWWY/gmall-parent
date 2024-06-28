package com.example.gmall.model.order.vo;

import lombok.Data;

import java.util.List;

/**
 * @author lfy
 * @Description
 * @create 2022-12-26 11:04
 */
//[{"wareId":"1","skuIds":["50"]},{"wareId":"2","skuIds":["49"]}]
@Data
public class SkuWare {

    private Long wareId;
    private List<Long> skuIds;
}
