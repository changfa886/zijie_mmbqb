package com.xagent.dyin.aop_repeat_tokencache;

import com.google.common.cache.Cache;
import org.apache.http.util.TextUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Aspect
@Component
/**
 * @功能描述 aop解析注解
 */
public class NoRepeatSubmitTokenCacheAop
{
    private Logger logger = LoggerFactory.getLogger(NoRepeatSubmitTokenCacheAop.class);
    @Autowired
    private Cache<String, Integer> cache;

    @Around("execution(* com.xagent..*Controller.*(..)) && @annotation(nrsc)")
    public Object arround(ProceedingJoinPoint pjp, NoRepeatSubmitTokenCache nrsc) throws Throwable
    {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String token = request.getHeader("Token");
        if (TextUtils.isEmpty(token))
        {
            Object o = pjp.proceed();
            return o;
        }

        String key = token + "-" +request.getServletPath();

        // 如果缓存中有这个url视为重复提交
        if (cache.getIfPresent(key) == null)
        {
            Object o = pjp.proceed();
            cache.put(key, 0);
            return o;
        }
        else
        {
            logger.error("重复提交");
            return null;
        }

    }

}