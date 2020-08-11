package com.xagent.dyin.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 跨域配置 方案2:需通过addInterceptor引入才能生效. nginx前端跨域的配置，对文件上传无效，故here
@Component
public class CorsDomainIntercept implements HandlerInterceptor
{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws IOException, ServletException
    {
        // 当Access-Control-Allow-Credentials设置为ture时，Access-Control-Allow-Origin不能设置为*
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");  // POST, GET, OPTIONS, DELETE
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Origin, X-Requested-With, Content-Type, Accept, Access-Token, Token");

        return true;
    }

    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
            throws Exception {
    }

    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2)
            throws Exception {}
}
