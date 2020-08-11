package com.xagent.dyin.config_redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xagent.dyin.utils.Md5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 在SpringBoot2.0之后，spring容器是自动的生成了StringRedisTemplate和RedisTemplate<Object,Object>，可以直接注入
 但是在实际使用中，我们大多不会直接使用RedisTemplate<Object,Object>，而是会对key,value进行序列化，
 所以我们还需要新增一个配置类
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport
{
    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Resource
    private LettuceConnectionFactory lettuceConnectionFactory;

    // 自定义缓存key的生成策略。默认的生成策略是看不懂的(乱码内容)
    // 通过Spring 的依赖注入特性进行自定义的配置注入并且此类是一个配置类可以更多程度的自定义配置
    @Bean
    public KeyGenerator myKeyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append("cache.");
            sb.append(target.getClass().getName());
            sb.append(method.getName());
            for (Object obj : params) {
                sb.append(obj.toString());
            }
            String xkey = Md5Utils.MD5Encode(sb.toString());
            // logger.info("xkey === "+xkey);
            return xkey;
        };
    }

    // 缓存管理器 使用Lettuce，和jedis有很大不同
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        //关键点，spring cache的注解使用的序列化都从这来，没有这个配置的话使用的jdk自己的序列化，实际上不影响使用，只是打印出来不适合人眼识别
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))//key序列化方式
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer()))//value序列化方式
                .disableCachingNullValues()
                .entryTtl(Duration.ofSeconds(10));//缓存过期时间

        RedisCacheConfiguration twentyConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))//key序列化方式
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer()))//value序列化方式
                .disableCachingNullValues()
                .entryTtl(Duration.ofSeconds(20));//缓存过期时间

        RedisCacheConfiguration oneminuteConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))//key序列化方式
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer()))//value序列化方式
                .disableCachingNullValues()
                .entryTtl(Duration.ofSeconds(60));//缓存过期时间
        RedisCacheConfiguration tenminuteConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))//key序列化方式
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer()))//value序列化方式
                .disableCachingNullValues()
                .entryTtl(Duration.ofSeconds(600));


        //缓存配置map
        Map<String,RedisCacheConfiguration> cacheConfigurationMap=new HashMap<>();
        //自定义缓存名，后面使用的@Cacheable的CacheName
        cacheConfigurationMap.put("twentysec",twentyConfig);
        cacheConfigurationMap.put("default",defaultConfig);
        cacheConfigurationMap.put("oneminute",oneminuteConfig);
        cacheConfigurationMap.put("tenminute",tenminuteConfig);

        //根据redis缓存配置和reid连接工厂生成redis缓存管理器
//        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.RedisCacheManagerBuilder
//                .fromConnectionFactory(connectionFactory)
//                .cacheDefaults(config)
//                .transactionAware()
//                .withInitialCacheConfigurations(cacheConfigurationMap);
//
//        return builder.build();

        RedisCacheManager redisCacheManager = RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .transactionAware()
                .withInitialCacheConfigurations(cacheConfigurationMap)
                .build();
        return redisCacheManager;
    }

    /**
     * RedisTemplate配置 在单独使用redisTemplate的时候 重新定义序列化方式
     */
    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        // 配置redisTemplate
        // ?注释掉的两行代码不能打开。打开的话会使value序例化，序例化了以后会跟后面要使用的@Cacheable不一致，导致取不到值?
        // 已更新Cacheable中的序列化方式,与这里的序列化方式一致了
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        // lettuceConnectionFactory.setDatabase(0); //切换redis库的地方在此 配置文件yml中也有设置
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);

        RedisSerializer<?> stringSerializer = keySerializer();
        RedisSerializer<Object> valueSerializer = valueSerializer();
        redisTemplate.setKeySerializer(stringSerializer);// key序列化
        redisTemplate.setValueSerializer(valueSerializer);// value序列化 注释
        redisTemplate.setHashKeySerializer(stringSerializer);// Hash key序列化
        redisTemplate.setHashValueSerializer(valueSerializer);// Hash value序列化 注释
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
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

        //或者使用GenericJackson2JsonRedisSerializer
        //return new GenericJackson2JsonRedisSerializer();
    }

}
