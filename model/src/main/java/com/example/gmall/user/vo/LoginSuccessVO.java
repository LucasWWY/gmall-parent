package com.example.gmall.user.vo;

import lombok.Data;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 15/5/2024 - 5:12 am
 * @Description
 */
@Data
public class LoginSuccessVO {
    private String token;
    private Long userId;
    private String nickName;
}
