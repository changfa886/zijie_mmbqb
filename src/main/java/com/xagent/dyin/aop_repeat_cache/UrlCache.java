package com.xagent.dyin.aop_repeat_cache;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@EnableCaching
@Configuration
/**
 * @功能描述 内存缓存 适用于单机模式
 */
public class UrlCache {
    @Bean
    public Cache<String, Integer> getCache()
    {
        return CacheBuilder.newBuilder().expireAfterWrite(2L, TimeUnit.SECONDS).build();// 缓存有效期为2秒
    }
}