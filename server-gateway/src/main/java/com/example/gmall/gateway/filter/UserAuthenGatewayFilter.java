package com.example.gmall.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.example.gmall.common.constant.RedisConst;
import com.example.gmall.common.result.Result;
import com.example.gmall.common.result.ResultCodeEnum;
import com.example.gmall.gateway.properties.AuthUrlProperties;
import com.example.gmall.user.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;



import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 19/5/2024 - 6:45 pm
 * @Description 拦截所有请求，透传userId
 */
@Slf4j
@Component
public class UserAuthenGatewayFilter implements GlobalFilter {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    AuthUrlProperties authUrlProperties;

    AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        UserInfo userInfo = null;

        log.info("请求：{}",request.getURI().getPath());

        //【1：静态资源放行】
        //如果是静态资源，直接放行 无需查询用户身份信息 如：/img/cartPanelViewIcons.png
        long count = authUrlProperties.getAnyoneUrl() //pattern来自app: auth: anyone-url: - /css/** - /js/** - /img/**
                .stream()
                .filter(pattern -> pathMatcher.match(pattern, path))//当前：/css/all.css 是否匹配：/css/**
                .count();
        
        if (count > 0) {
            log.info("静态资源直接放行....");
            return chain.filter(exchange); //也是Mono
        }

        //【2：直接拒绝访问的路径】
        long denyCount = authUrlProperties.getDenyUrl()
                .stream()
                .filter(pattern -> pathMatcher.match(pattern, path))
                .count();
        if (denyCount > 0) {
            log.warn("浏览器请求内部路径，疑似攻击，直接打回");
            Result<String> result = Result.build("", ResultCodeEnum.PERMISSION);
            //返回错误json
            return responseJson(exchange, result); //响应式编程要返回mono，所以responseJson返回mono
        }

        //3、【有限权限访问】  必须登录才访问
        long authenCount = authUrlProperties.getAuthenUrl()
                .stream()
                .filter(pattern -> pathMatcher.match(pattern, path))
                .count();
        if (authenCount > 0) {
            //能拿到令牌说明登录了，否则没登录
            String token = getToken(exchange); //获取请求头或者cookie中携带的token 见md的Token不同携带方式
            userInfo = getUserInfo(token);
            if (StringUtils.isEmpty(token) || userInfo == null) { //为什么能为null？因为令牌带了不一定是真的，redis找不到对应的用户（一定没登录 有没有这个用户是另一码事）
                //浏览器重定向到登录页去登录
                return redirectToPage(exchange, authUrlProperties.getLoginPage());
            }
        }

        //4、 用户没带token / 用户真的登录了
        //4.1 用户没带token，userInfo在if (authenCount > 0)得到null，但此时还有userTempId需要透传 给购物车使用
        //4.2 用户真的登录了，userInfo在if (authenCount > 0)得到用户信息，此时userId需要透传 给下游服务使用
        return userIdThrough(chain, exchange, userInfo); //用户id穿透: 从网关 -> 其他服务


        //SpringBoot的响应式编程 - 放行
        //Mono<Void> filter = chain.filter(exchange)
        //                .doFinally((item) -> {
        //                    log.info("请求结束：{}", request.getURI().toString());
        //});

        //CompletableFuture<Void> future = CompletableFuture.runAsync(() -> { //传统异步编程使用CompletableFuture执行异步任务，并在任务完成时提供一种获取其结果的方法
        //});
    }


    private Mono<Void> userIdThrough(GatewayFilterChain chain, ServerWebExchange exchange, UserInfo userInfo) {
        //1、透传: 请求的数据都是只读的不能修改
        ServerHttpRequest.Builder reqBuilder = exchange.getRequest()
                .mutate(); //所以gate要用decorator去wrap原有的request，然后通过mutate decorator去覆盖request的properties

        //exchange.getRequest().getHeaders().add("userId", userInfo.getId().toString()); 是不行的
        //exchange.getRequest()
        //        .mutate()
        //        .header("userId", userInfo.getId().toString())
        //        .header("userTempId", getTempId(exchange))
        //        .build();

        //2、用户id放在请求头
        if(userInfo!=null){
            log.info("用户信息放在头中，往下透传");
            //只要构造完成，老的exchange里面的request也会跟着变
            reqBuilder.header(RedisConst.USER_ID_HEADER, userInfo.getId().toString())
                    .build();
        }

        //3、临时id：用于临时用户会话管理。它通常在用户未登录的情况下使用，用于追踪匿名用户的行为，例如购物车、浏览记录等
        String tempId = getTempId(exchange);
        if(!StringUtils.isEmpty(tempId)){
            log.info("用户临时信息放在头中，往下透传");
            //只要构造完成，老的exchange里面的request也会跟着变
            reqBuilder.header(RedisConst.USER_TEMP_ID_HEADER, tempId)
                    .build();
        }

        //4、放行
        return chain.filter(exchange);
    }

    private String getTempId(ServerWebExchange exchange) {
        //1、拿到请求
        ServerHttpRequest request = exchange.getRequest();
        //2、先看请求头（ajax请求）
        String token = request.getHeaders().getFirst("userTempId");
        if (!StringUtils.isEmpty(token)) {
            return token;
        }
        //3、如果头没有，就看cookie（非ajax请求）
        HttpCookie first = request.getCookies().getFirst("userTempId");
        if (first != null) {
            return first.getValue();
        }
        return null;
    }

    /**
     * 重定向到登录页 浏览器 会根据 响应状态码 + 响应头的Location 重新发起请求
     *
     * @param exchange
     * @param loginPage
     * @return
     */
    private Mono<Void> redirectToPage(ServerWebExchange exchange, String loginPage) {
        ServerHttpResponse response = exchange.getResponse();
        //当时的请求路径
        URI uri = exchange.getRequest().getURI();

        //设置好要跳转的地址 //http://passport.gmall.com/login.html?originUrl=http://gmall.com/ 还要通过参数把原始请求的地址带过去
        loginPage += "?originUrl=" + uri.toString();

        //1、设置响应状态码  302
        response.setStatusCode(HttpStatus.FOUND);

        //2、设置响应头  Location: 新位置  http://passport.gmall.com/login.html
        response.getHeaders().set("Location", loginPage);
        return response.setComplete(); //响应式编程 需要返回Mono
    }

    /**
     * 响应json
     *
     * @param exchange
     * @param result
     * @return
     */
    private Mono<Void> responseJson(ServerWebExchange exchange, Result result) {

        //1、得到响应对象
        ServerHttpResponse response = exchange.getResponse();

        //2、得到数据的DataBuffer
        String json = JSON.toJSONString(result);
        DataBuffer dataBuffer = response
                .bufferFactory()
                .wrap(json.getBytes(StandardCharsets.UTF_8)); //把result的JSON包装字节成为数据缓冲区

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8); //告诉browser响应的内容的 格式

        return response.writeWith(Mono.just(dataBuffer));
    }

    private UserInfo getUserInfo(String token) {
        if(StringUtils.isEmpty(token)){
            return null;
        }
        String json = redisTemplate.opsForValue().get(RedisConst.LOGIN_USER + token);
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        UserInfo userInfo = JSON.parseObject(json, UserInfo.class);
        return userInfo;
    }

    /**
     * 获取请求头或者cookie中携带的用户令牌标识
     * @param exchange
     * @return
     */
    private String getToken(ServerWebExchange exchange) {
        //1、拿到请求
        ServerHttpRequest request = exchange.getRequest();
        //2、先看请求头
        String token = request.getHeaders().getFirst("token");
        if (!StringUtils.isEmpty(token)) {
            return token;
        }

        //3、如果头没有，就看cookie； 有可能没登录这个cookie都没有
        HttpCookie first = request.getCookies().getFirst("token");
        if (first != null) {
            return first.getValue();
        }
        return null;
    }
}
