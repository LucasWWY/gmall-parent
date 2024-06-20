package com.example.gmall.gateway.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 19/5/2024 - 6:47 pm
 * @Description JavaWeb的Filter
 */
public class HelloFilter implements Filter{
//public class HelloFilter extends HttpFilter { //HttpFilter is an abstract class that extends Filter

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        //放行请求，才能到servlet，servlet处理完了，又会把响应再过滤一次
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
