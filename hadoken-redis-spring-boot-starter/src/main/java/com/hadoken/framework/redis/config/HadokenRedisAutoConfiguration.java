package com.hadoken.framework.redis.config;

import cn.hutool.core.util.CharUtil;
import cn.hutool.json.JSONUtil;
import com.hadoken.framework.redis.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Redis 配置类
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/03/02 11:06
 */
@Slf4j
@AutoConfiguration
@EnableCaching
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableConfigurationProperties(HadokenRedisProperties.class)
public class HadokenRedisAutoConfiguration {

    /**
     * 配置 key 过期时间
     */
    @Bean
    @ConditionalOnBean({RedisCacheConfiguration.class})
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory,
                                               RedisCacheConfiguration redisCacheConfiguration,
                                               HadokenRedisProperties properties) {

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // 将 key-ttl 映射为缓存名规则（支持前缀匹配）
        properties.getKeyTtl().forEach((pattern, ttl) -> {
            RedisCacheConfiguration conf = redisCacheConfiguration.entryTtl(ttl);
            // 使用通配符生成缓存名规则，如 user:* -> userCache
            cacheConfigurations.put(pattern.replace("*", ""), conf);
        });

        log.info("RedisCacheManager has been configured");
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory,
                                                       HadokenRedisProperties properties) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // key 和 hashKey 用 String 序列化
        template.setKeySerializer(properties.getKeySerializer());
        template.setHashKeySerializer(properties.getHashKeySerializer());

        // value 和 hashValue 用 JSON 序列化
        template.setValueSerializer(properties.getValueSerializer());
        template.setHashValueSerializer(properties.getHashValueSerializer());

        // 必须调用，否则配置不生效
        template.afterPropertiesSet();

        log.info("RedisTemplate has been configured");
        return template;
    }

    @Bean
//    @ConditionalOnMissingBean
//    @ConditionalOnBean(RedisConnectionFactory.class)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(factory);
        log.info("StringRedisTemplate has been configured");
        return template;
    }

    /**
     * 配置 redis 工具类
     *
     * @return {@link RedisUtils}
     */
    @Bean
//    @ConditionalOnMissingBean
    public RedisUtils redisUtils(ObjectProvider<RedisTemplate<String, Object>> redisTemplateProvider,
                                 ObjectProvider<StringRedisTemplate> stringRedisTemplateProvider) {
        return new RedisUtils(
                redisTemplateProvider.getIfAvailable(),
                stringRedisTemplateProvider.getIfAvailable()
        );
    }

    /**
     * 自定义缓存 key 生成策略，默认将使用该策略
     */
    @Bean
    public KeyGenerator keyGenerator() {
        log.info("keyGenerator has been configured");
        return (target, method, params) -> {
            Map<String, Object> container = new HashMap<>(3);
            Class<?> targetClassClass = target.getClass();
            // 类地址
            container.put("class", targetClassClass.toGenericString());
            // 方法名称
            container.put("methodName", method.getName());
            // 包名称
            container.put("package", targetClassClass.getPackage());
            // 参数列表
            for (int i = 0; i < params.length; i++) {
                container.put(String.valueOf(i), params[i]);
            }
            // 转为JSON字符串
            String jsonString = JSONUtil.toJsonStr(container);
            // 做SHA256 Hash计算，得到一个SHA256摘要作为Key
            return targetClassClass.getSimpleName()
                    + CharUtil.COLON
                    + method.getName()
                    + CharUtil.COLON
                    + DigestUtils.sha256Hex(jsonString);
        };
    }


    /**
     * ❗️重点：自定义 CacheErrorHandler（以前通过 @Override 实现）
     * 现在通过 @Bean 注册一个 CacheErrorHandler 实例
     */
    @SuppressWarnings("NullableProblems")
    @Bean
    public CacheErrorHandler cacheErrorHandler() {
        log.info("初始化 -> Redis CacheErrorHandler");

        return new SimpleCacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                log.error("Redis Cache - Get Error, key: [{}], cache: [{}]", key, cache.getName(), e);
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
                log.error("Redis Cache - Put Error, key: [{}], value: [{}], cache: [{}]", key, value, cache.getName(), e);
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
                log.error("Redis Cache - Evict Error, key: [{}], cache: [{}]", key, cache.getName(), e);
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                log.error("Redis Cache - Clear Error, cache: [{}]", cache.getName(), e);
            }
        };
    }
}
