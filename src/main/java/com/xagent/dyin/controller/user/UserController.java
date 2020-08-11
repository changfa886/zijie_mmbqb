package com.xagent.dyin.controller.user;

import com.xagent.dyin.config_redis.RedisUtil;
import com.xagent.dyin.db.service.ZijieUserService;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

public class UserController
{
    @Autowired
    protected ZijieUserService zijieUserService;
    @Autowired
    protected RedisUtil redisUtil;

    // 根据session得到 uid
    public Long getUidFromToken(String token)
    {
        long uid = 0;
        Object objUid = redisUtil.get("zjut_"+token);
        if (objUid != null)
        {
            uid = Long.parseLong(objUid.toString());
        }

        return uid;
    }

    public String getIpAddr(HttpServletRequest request)
    {
        String ip = request.getHeader("x-forwarded-for");
//		String ip = request.getHeader("Proxy-Client-IP");//处理IP
        if (TextUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (TextUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (TextUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (TextUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (TextUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (TextUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            //X-Real-IP：nginx服务代理
            ip = request.getHeader("X-Real-IP");
        }

        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip))
        {
            // 多次反向代理后会有多个IP值，第一个为真实IP。
            int index = ip.indexOf(',');
            if (index != -1)
            {
                return ip.substring(0, index);
            }
            else
            {
                return ip;
            }
        }
        return ip;
    }
}
