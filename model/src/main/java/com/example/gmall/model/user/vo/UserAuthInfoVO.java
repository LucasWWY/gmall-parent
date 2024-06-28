package com.example.gmall.model.user.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 23/6/2024 - 8:49 pm
 * @Description
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserAuthInfoVO {
    private Long userId;
    private Long userTempId;
}
