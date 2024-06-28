package com.example.gmall.model.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName order_status_log
 */
@TableName(value ="order_status_log")
@Data
public class OrderStatusLog implements Serializable {
    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 订单状态
     */
    private String orderStatus;

    /**
     * 修改时间
     */
    private Date operateTime;

    /**
     * 
     */
    private Long userId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}