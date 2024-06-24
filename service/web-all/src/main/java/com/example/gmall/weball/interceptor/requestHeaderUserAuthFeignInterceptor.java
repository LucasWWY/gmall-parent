package com.example.gmall.weball.interceptor;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 23/6/2024 - 1:45 am
 * @Description 透传header信息，把网关传递的请求中的header信息透传给feign创建的新请求调用
 */
//@Slf4j
//@Component //因为synchronousMethodHandler只有一个有参构造器，所以只能通过容器 得到需要的参数，所以这个类必须是spring管理
//public class  requestHeaderUserAuthFeignInterceptor implements RequestInterceptor {
//
//    @Override
//    public void apply(RequestTemplate requestTemplate) {
//
//        //获取了request中的header信息
//        //HttpServletRequest request = CartController.threadLocal.get(); //代替HttpServletRequest request = CartController.requestMap.get(Thread.currentThread());
//
//        //ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        //HttpServletRequest request = attributes.getRequest();
//        //String userId = request.getHeader(RedisConst.USER_ID_HEADER);
//        //String userTempId = request.getHeader(RedisConst.USER_TEMP_ID_HEADER);
//        //重复使用 封装起来
//        UserAuthInfoVO info = UserAuthUtils.getUserAuthInfo();
//
//        requestTemplate.header(RedisConst.USER_ID_HEADER, String.valueOf(info.getUserId()));
//        requestTemplate.header(RedisConst.USER_TEMP_ID_HEADER, String.valueOf(info.getUserTempId()));
//
//        //此后利用template建立的新请求，都会带有userId和userTempId
//        //完成了userId和userTempId的隐式透传
//        //CartController中: Result<AddCartSuccessVO> result = cartFeignClient.addToCart(skuId, skuNum) 只传递了skuId, skuNum
//    }
//    //
//}
