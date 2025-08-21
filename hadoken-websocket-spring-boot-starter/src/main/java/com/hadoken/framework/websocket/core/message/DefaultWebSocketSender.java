package com.hadoken.framework.websocket.core.message;

import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.hadoken.framework.websocket.config.WebSocketProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * websocket 消息发送器
 */
@Slf4j
public class DefaultWebSocketSender implements WebSocketSender {

    // 设置时间格式化
    private static final JSONConfig DEFAULT_JSON_CONFIG = JSONConfig.create()
            .setIgnoreNullValue(false)
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .setIgnoreError(true);

    private static final JSONConfig JSON_IGNORE_NULL = JSONConfig.create()
            .setIgnoreNullValue(true)
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .setIgnoreError(true);

    protected SimpMessagingTemplate simpMessagingTemplate;
    protected WebSocketProperties webSocketProperties;

    public DefaultWebSocketSender(SimpMessagingTemplate simpMessagingTemplate, WebSocketProperties webSocketProperties) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.webSocketProperties = webSocketProperties;
    }

    /**
     * 通过 ws 配置项中来查找 topic
     */
    public <T> Future<Boolean> sendByKey(String key, T payload) {
        return sendByKey(key, payload, false);
    }

    @Override
    public <T> Future<Boolean> sendByKey(String key, T payload, boolean ignoreNull) {
        String jsonStr = parseToJsonStr(payload, ignoreNull);
        return sendByKey(key, jsonStr);
    }

    @Override
    public Future<Boolean> sendByKey(String key, String payload) {
        String topic = webSocketProperties.getTopicByKey(key);
        log.debug("sendWithTopic >> topic:[{}], payload:[{}] ", topic, payload);
        return sendWithTopic(topic, payload);
    }

    /**
     * 传入 topic 直接发送
     *
     * @param topic   消息主题
     * @param payload 消息体
     * @return Future
     */
    public <T> Future<Boolean> sendWithTopic(String topic, T payload) {
        return sendWithTopic(topic, payload, false);
    }

    @Override
    public <T> Future<Boolean> sendWithTopic(String topic, T payload, boolean ignoreNull) {
        String jsonStr = parseToJsonStr(payload, ignoreNull);
        return sendWithTopic(topic, jsonStr);
    }

    @Override
    public Future<Boolean> sendWithTopic(String topic, String payload) {
        log.debug("sendWithTopic begin >> topic:[{}], payload:[{}] ", topic, payload);
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        try {
            simpMessagingTemplate.convertAndSend(topic, payload);
            log.debug("sendWithTopic end << topic:[{}], payload:[{}] ", topic, payload);
            future.complete(true);
        } catch (Exception e) {
            log.error("推送消息失败:topic-[{}],推送内容-[{}],异常信息-[{}]", topic, payload, e.getMessage());
            future.complete(false);
        }
        return future;
    }

    private static <T> String parseToJsonStr(T payload, boolean ignoreNull) {
        String jsonStr;
        if (payload instanceof String) {
            jsonStr = (String) payload;
        } else {
            if (ignoreNull) {
                jsonStr = JSONUtil.parse(payload, JSON_IGNORE_NULL).toString();
            } else {
                jsonStr = JSONUtil.parse(payload, DEFAULT_JSON_CONFIG).toString();
            }
        }
        return jsonStr;
    }
}