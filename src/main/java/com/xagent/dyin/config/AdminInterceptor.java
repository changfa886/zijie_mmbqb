package com.xagent.dyin.config;

import com.alibaba.fastjson.JSON;
import com.xagent.dyin.db.entity.ZijieAdminEntity;
import com.xagent.dyin.db.service.ZijieAdminService;
import com.xagent.dyin.entity.MyResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Configuration
public class AdminInterceptor implements HandlerInterceptor
{
    private static Logger logger = LoggerFactory.getLogger(AdminInterceptor.class);

    @Autowired
    private ZijieAdminService zijieAdminService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        // /boss/agent/query/info
        String reqUrl = request.getRequestURI();

        long adm_id = 0;
        HttpSession session = request.getSession();
        Object adminIdObj = session.getAttribute("admin_id");
        if (adminIdObj == null)
        {
            //return true; // 交由controller处理

            MyResp myResp = new MyResp(1001);
            response.setHeader("content-type","text/html;charset=utf-8");
            response.getWriter().write(JSON.toJSONString(myResp));
            return false;
        }

        adm_id = Long.parseLong(adminIdObj.toString());
        if (adm_id <= 0)
        {
            MyResp myResp = new MyResp(1001);
            response.setHeader("content-type","text/html;charset=utf-8");
            response.getWriter().write(JSON.toJSONString(myResp));
            return false;
        }

        if (true)
        {
            return true;
        }

        ZijieAdminEntity adminEntity = zijieAdminService.getById(adm_id);
        if (adminEntity == null)
        {
            MyResp myResp = new MyResp(1001);
            response.setHeader("content-type","text/html;charset=utf-8");
            response.getWriter().write(JSON.toJSONString(myResp));
            return false;
        }

        if (adminEntity.getRole().equals("kefu"))
        {
            if (reqUrl.startsWith("/admin/account/xxx"))
            {
                logger.info(adminEntity.getAdmname()+ " 无权限: "+reqUrl);
                MyResp myResp = new MyResp(1000, "无权限");
                response.setHeader("content-type","text/html;charset=utf-8");
                response.getWriter().write(JSON.toJSONString(myResp));
                return false;
            }
        }

        return true;
    }
}
