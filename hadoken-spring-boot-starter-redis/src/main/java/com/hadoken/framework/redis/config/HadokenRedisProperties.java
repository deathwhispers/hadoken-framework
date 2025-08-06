package com.hadoken.framework.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/5 09:05
 */
@Data
@ConfigurationProperties(prefix = "hadoken.redis")
public class HadokenRedisProperties {
    /**
     * 是否启用 Rhy Redis 扩展
     */
    private boolean enabled = true;

    /**
     * 支持按 key 或 key 前缀设置 TTL
     * 支持通配符 *，如 user:*、product:detail:*
     */
    private final Map<String, Duration> keyTtl = new HashMap<>();

    /**
     * redisTemplate key 的序列化方式
     */
    private RedisSerializer keySerializer = RedisSerializer.string();
    /**
     * redisTemplate hash key 的序列化方式
     */
    private RedisSerializer hashKeySerializer = RedisSerializer.string();
    /**
     * redisTemplate value 的序列化方式
     */
    private RedisSerializer valueSerializer = RedisSerializer.json();
    /**
     * redisTemplate hash value 的序列化方式
     */
    private RedisSerializer hashValueSerializer = RedisSerializer.json();

}
