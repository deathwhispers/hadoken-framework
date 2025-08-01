package com.hadoken.framework.mq.config;

import com.hadoken.framework.mq.core.RedisMQTemplate;
import com.hadoken.framework.mq.core.interceptor.RedisMessageInterceptor;
import com.hadoken.framework.mq.core.pubsub.AbstractChannelMessageListener;
import com.hadoken.framework.redis.config.HadokenRedisAutoConfiguration;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.List;

/**
 * 消息队列配置类
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/03/02 11:48
 */
@Slf4j
@Configuration
@AutoConfigureAfter(HadokenRedisAutoConfiguration.class)
public class HadokenMQAutoConfiguration {

    @Resource
    private StringRedisTemplate redisTemplate;

    @Bean
    public RedisMQTemplate redisMQTemplate(List<RedisMessageInterceptor> interceptors) {
        RedisMQTemplate redisMQTemplate = new RedisMQTemplate(redisTemplate);

        // 添加拦截器
        interceptors.forEach(redisMQTemplate::addInterceptor);
        return redisMQTemplate;
    }

    // ========== 消费者相关 ==========

    /**
     * 创建 Redis Pub/Sub 广播消费的容器
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisMQTemplate redisMQTemplate, List<AbstractChannelMessageListener<?>> listeners) {
        // 创建 RedisMessageListenerContainer 对象
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        // 设置 RedisConnection 工厂。
        container.setConnectionFactory(redisMQTemplate.getRedisTemplate().getRequiredConnectionFactory());
        // 添加监听器
        listeners.forEach(listener -> {
            listener.setRedisMQTemplate(redisMQTemplate);
            container.addMessageListener(listener, new ChannelTopic(listener.getChannel()));
            log.info("[redisMessageListenerContainer][注册 Channel({}) 对应的监听器({})]",
                    listener.getChannel(), listener.getClass().getName());
        });
        return container;
    }

}
