package com.example.gmall.common.authen;

import com.example.gmall.common.constant.RedisConst;
import com.example.gmall.user.vo.UserAuthInfoVO;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 23/6/2024 - 8:47 pm
 * @Description
 */
public class UserAuthUtils {

    public static UserAuthInfoVO getUserAuthInfo() {
        //RequestContextHolder用于在同一个JVM内共享当前请求的上下文信息，这在单体应用中非常有用。但在分布式微服务架构中，不同服务可能运行在不同的JVM中，RequestContextHolder无法跨越JVM边界。
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String userId = request.getHeader(RedisConst.USER_ID_HEADER);
        String userTempId = request.getHeader(RedisConst.USER_TEMP_ID_HEADER);

        Long userIdl = null;
        Long userTempIdl = null;

        try {
            userIdl = Long.parseLong(userId);
            userTempIdl = Long.parseLong(userTempId);
        } catch (Exception e) {

        }

        return new UserAuthInfoVO(userIdl, userTempIdl);
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        return request;
    }

}
