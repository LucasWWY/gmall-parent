package com.example.gmall.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 20/5/2024 - 1:41 am
 * @Description
 */
@Component
@ConfigurationProperties(prefix = "app.authen")
@Data
public class AuthUrlProperties {

        //直接放行的请求
        private List<String> anyoneUrl;

        //任何情况都拒绝访问的
        private List<String> denyUrl;

        //必须认证通过（登录以后）才能访问的路径
        private List<String> authenUrl;

        private String loginPage; //登录页地址
}
