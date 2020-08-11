package com.xagent.dyin.config_redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;
import java.time.Duration;

/**
 * Created by jonty on 2019/8/11.
 */

/**
 * 缓存配置-使用Lettuce客户端，自动注入配置的方式
 */

//@Configuration
//@EnableCaching
public class RedisCacheConfig extends CachingConfigurerSupport
{
    /**
     * 自定义缓存key的生成策略。默认的生成策略是看不懂的(乱码内容) 通过Spring 的依赖注入特性进行自定义的配置注入并且此类是一个配置类可以更多程度的自定义配置
     *
     * @return
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }

    /**
     * 缓存配置管理器
     */
    @Bean
    public CacheManager cacheManager(LettuceConnectionFactory factory) {
        //以锁写入的方式创建RedisCacheWriter对象
        RedisCacheWriter writer = RedisCacheWriter.lockingRedisCacheWriter(factory);
        /*
        设置CacheManager的Value序列化方式为JdkSerializationRedisSerializer,
        但其实RedisCacheConfiguration默认就是使用
        StringRedisSerializer序列化key，
        JdkSerializationRedisSerializer序列化value,
        所以以下注释代码就是默认实现，没必要写，直接注释掉
         */
//         RedisSerializationContext.SerializationPair pair = RedisSerializationContext.SerializationPair.fromSerializer(new JdkSerializationRedisSerializer(this.getClass().getClassLoader()));
//         RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(pair);
        // 创建默认缓存配置对象
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        // 对key和value分别进行序列化
        config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()));//key序列化方式
        config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer()));//value序列化方式
        config.disableCachingNullValues();
        config.entryTtl(Duration.ofSeconds(60));

        RedisCacheManager cacheManager = new RedisCacheManager(writer, config);
        return cacheManager;
    }

    /**
     * 获取缓存操作助手对象
     *
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory factory)
    {
        //创建Redis缓存操作助手RedisTemplate对象
        RedisTemplate template = new RedisTemplate();
        //factory.setDatabase(0); //切换库的地方在此 配置文件yml中也有设置
        template.setConnectionFactory(factory);
        template.setKeySerializer(keySerializer());  // 这个地方是关键,key序列化的方式,影响 redis-cli的查询
        template.setValueSerializer(valueSerializer());
        template.afterPropertiesSet();
        return template;
    }

    // 这个地方是关键,通过该config的redis,可以通过redis-cli直接查询到key
    private RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }

    // 将RedisTemplate的Value序列化方式由JdkSerializationRedisSerializer更换为Jackson2JsonRedisSerializer
    private RedisSerializer<Object> valueSerializer() {
        // 设置序列化
        // 将RedisTemplate的Value序列化方式由JdkSerializationRedisSerializer更换为 Jackson2JsonRedisSerializer
        // 此种序列化方式结果清晰、容易阅读、存储字节少、速度快，所以推荐更换
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(
                Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        return jackson2JsonRedisSerializer;
    }
}
