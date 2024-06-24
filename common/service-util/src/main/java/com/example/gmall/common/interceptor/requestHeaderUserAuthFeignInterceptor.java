package com.example.gmall.common.interceptor;

import com.example.gmall.common.authen.UserAuthUtils;
import com.example.gmall.common.constant.RedisConst;
import com.example.gmall.user.vo.UserAuthInfoVO;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 23/6/2024 - 10:14 pm
 * @Description
 */
@Slf4j
@Component //因为synchronousMethodHandler只有一个有参构造器，所以只能通过容器 得到需要的参数，所以这个类必须是spring管理
public class  requestHeaderUserAuthFeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {

        //获取了request中的header信息
        //HttpServletRequest request = CartController.threadLocal.get(); //代替HttpServletRequest request = CartController.requestMap.get(Thread.currentThread());

        //ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //HttpServletRequest request = attributes.getRequest();
        //String userId = request.getHeader(RedisConst.USER_ID_HEADER);
        //String userTempId = request.getHeader(RedisConst.USER_TEMP_ID_HEADER);
        //重复使用 封装起来
        UserAuthInfoVO info = UserAuthUtils.getUserAuthInfo();


        requestTemplate.header(RedisConst.USER_ID_HEADER, String.valueOf(info.getUserId()));
        requestTemplate.header(RedisConst.USER_TEMP_ID_HEADER, String.valueOf(info.getUserTempId()));

        //此后利用template建立的新请求，都会带有userId和userTempId
        //完成了userId和userTempId的隐式透传
        //CartController中: Result<AddCartSuccessVO> result = cartFeignClient.addToCart(skuId, skuNum) 只传递了skuId, skuNum
    }
    //
}
