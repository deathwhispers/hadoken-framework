package com.hadoken.framework.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

/**
 * Redis 配置类
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/03/02 11:06
 */
@Slf4j
@Configuration
public class HadokenRedisAutoConfiguration extends CachingConfigurerSupport {

    /**
     * 设置 redis 数据默认过期时间，默认2小时
     * 设置@cacheable 序列化方式
     */
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig();
        configuration = configuration.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer)).entryTtl(Duration.ofHours(2));
        return configuration;
    }

    /**
     * 创建 RedisTemplate Bean，使用 JSON 序列化方式
     */
    @Bean(name = "redisTemplate")
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

        // 创建 RedisTemplate 对象
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        // 设置 RedisConnection 工厂。
        template.setConnectionFactory(factory);

        // 使用 String 序列化方式，序列化 KEY 。
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());

        // 使用 JSON 序列化方式（库是 Jackson ），序列化 VALUE 。
        template.setValueSerializer(RedisSerializer.json());
        template.setHashValueSerializer(RedisSerializer.json());
        return template;
    }

    @Bean
    public RedissonConnectionFactory redissonConnectionFactory() {
        return new RedissonConnectionFactory();
    }

    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        // 异常处理，当Redis发生异常时，打印日志，但是程序正常走
        log.info("初始化 -> [{}]", "Redis CacheErrorHandler");
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                log.error("Redis occur handleCacheGetError：key -> [{}]", key, e);
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
                log.error("Redis occur handleCachePutError：key -> [{}]；value -> [{}]", key, value, e);
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
                log.error("Redis occur handleCacheEvictError：key -> [{}]", key, e);
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                log.error("Redis occur handleCacheClearError：", e);
            }
        };
    }
}
