package com.hadoken.framework.mq.core;

import com.hadoken.common.util.json.JsonUtils;
import com.hadoken.framework.mq.core.interceptor.RedisMessageInterceptor;
import com.hadoken.framework.mq.core.message.AbstractChannelMessage;
import com.hadoken.framework.mq.core.message.AbstractRedisMessage;
import com.hadoken.framework.mq.core.message.AbstractStreamMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Redis MQ 操作模板类
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/03/02 11:49
 */
@AllArgsConstructor
public class RedisMQTemplate {

    @Getter
    private final RedisTemplate<String, ?> redisTemplate;

    /**
     * 拦截器数组
     */
    @Getter
    private final List<RedisMessageInterceptor> interceptors = new ArrayList<>();

    /**
     * 发送 Redis 消息，基于 Redis pub/sub 实现
     *
     * @param message 消息
     */
    public <T extends AbstractChannelMessage> void send(T message) {
        try {
            sendMessageBefore(message);

            // 发送消息
            redisTemplate.convertAndSend(message.getChannel(), JsonUtils.toJsonString(message));
        } finally {
            sendMessageAfter(message);
        }
    }

    /**
     * 发送 Redis 消息，基于 Redis Stream 实现
     *
     * @param message 消息
     * @return 消息记录的编号对象
     */
    public <T extends AbstractStreamMessage> RecordId send(T message) {
        try {
            sendMessageBefore(message);

            // 发送消息
            return redisTemplate.opsForStream().add(StreamRecords.newRecord()

                    // 设置内容
                    .ofObject(JsonUtils.toJsonString(message))

                    // 设置 stream key
                    .withStreamKey(message.getStreamKey()));
        } finally {
            sendMessageAfter(message);
        }
    }

    /**
     * 添加拦截器
     *
     * @param interceptor 拦截器
     */
    public void addInterceptor(RedisMessageInterceptor interceptor) {
        interceptors.add(interceptor);
    }

    private void sendMessageBefore(AbstractRedisMessage message) {

        // 正序
        interceptors.forEach(interceptor -> interceptor.sendMessageBefore(message));
    }

    private void sendMessageAfter(AbstractRedisMessage message) {

        // 倒序
        for (int i = interceptors.size() - 1; i >= 0; i--) {
            interceptors.get(i).sendMessageAfter(message);
        }
    }

}
