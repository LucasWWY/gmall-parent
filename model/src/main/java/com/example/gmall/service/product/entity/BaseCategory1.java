package com.example.gmall.service.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 6/2/2024 - 7:08 pm
 * @Description
 */
@Data
@TableName("base_category1")
public class BaseCategory1 {

    @TableId(type = IdType.AUTO)
    public Long id;

    @TableField("name")
    private String name;
}
