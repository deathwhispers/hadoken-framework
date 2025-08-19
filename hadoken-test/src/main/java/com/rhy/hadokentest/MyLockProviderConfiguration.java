package com.rhy.hadokentest;

import com.hadoken.framework.scheduling.lock.DistributedLockProvider;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/14 20:46
 */
@Configuration
public class MyLockProviderConfiguration {

    @Bean
    public DistributedLockProvider distributedLockProvider(RedissonClient redissonClient) {
        return (lockKey, lockAtMostFor) -> {
            RLock lock = redissonClient.getLock("scheduler:" + lockKey);
            try {
                // Redisson的tryLock可以完美匹配我们的需求
                if (lock.tryLock(0, lockAtMostFor.toSeconds(), TimeUnit.SECONDS)) {
                    return Optional.of(lock::unlock);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return Optional.empty();
        };
    }
}