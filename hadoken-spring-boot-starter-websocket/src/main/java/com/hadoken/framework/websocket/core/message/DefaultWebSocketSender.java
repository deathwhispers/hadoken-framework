package com.hadoken.framework.websocket.core.message;

import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.hadoken.framework.websocket.config.WebSocketProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * websocket 消息发送器
 */
@Slf4j
@Component
public class DefaultWebSocketSender {

    // 设置时间格式化
    private static final JSONConfig jsonConfig = new JSONConfig()
            .setIgnoreNullValue(false)
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .setIgnoreError(true);

    @Resource
    protected SimpMessagingTemplate simpMessagingTemplate;
    @Resource
    protected WebSocketProperties webSocketProperties;

    /**
     * 通过 ws 配置项中来查找 topic
     */
    public <T> Future<Boolean> sendByKey(String key, T payload) {
        String topic = webSocketProperties.getTopicByKey(key);
        log.debug("getTopicByKey:{} >> topic:{}", key, topic);
        log.debug("send >> topic:[{}], payload:[{}] ", topic, payload);
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
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        try {
            simpMessagingTemplate.convertAndSend(topic, JSONUtil.toJsonStr(payload, jsonConfig));
            log.debug("sendWithTopic >> topic:[{}], payload:[{}] ", topic, payload);
            future.complete(true);
        } catch (Exception e) {
            log.error("推送消息失败:topic-[{}],推送内容-[{}],异常信息-[{}]", topic, payload, e.getMessage());
            future.complete(false);
        }
        return future;
    }

}