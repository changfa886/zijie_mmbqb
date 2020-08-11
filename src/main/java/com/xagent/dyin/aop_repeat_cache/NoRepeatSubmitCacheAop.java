package com.xagent.dyin.aop_repeat_cache;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.cache.Cache;

import java.util.HashMap;

@Aspect
@Component
/**
 * @功能描述 aop解析注解
 */
public class NoRepeatSubmitCacheAop
{
    private Logger logger = LoggerFactory.getLogger(NoRepeatSubmitCacheAop.class);
    @Autowired
    private Cache<String, Integer> cache;

    @Around("execution(* com.xagent..*Controller.*(..)) && @annotation(nrsc)")
    public Object arround(ProceedingJoinPoint pjp, NoRepeatSubmitCache nrsc) throws Throwable
    {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String sessionId = RequestContextHolder.getRequestAttributes().getSessionId();
        HttpServletRequest request = attributes.getRequest();
        String key = sessionId + "-" + request.getServletPath();
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