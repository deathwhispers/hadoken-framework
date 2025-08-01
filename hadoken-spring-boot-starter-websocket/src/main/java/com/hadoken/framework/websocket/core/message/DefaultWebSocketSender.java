package com.hadoken.framework.websocket.core.message;

import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.concurrent.Future;

/**
 * websocket 消息发送器
 */
public class DefaultWebSocketSender {
    private static final Logger logger = LoggerFactory.getLogger(DefaultWebSocketSender.class);
    // 设置时间格式化
    private static final JSONConfig jsonConfig = new JSONConfig()
            .setIgnoreNullValue(false)
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .setIgnoreError(true);

    protected final SimpMessagingTemplate simpMessagingTemplate;

    public DefaultWebSocketSender(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public <T> Future<Boolean> send(String topic, T payload) {
        try {
            String jsonStr = JSONUtil.parse(payload, jsonConfig).toString();
            simpMessagingTemplate.convertAndSend(topic, jsonStr);
            logger.debug("send >> topic:[{}], payload:[{}] ", topic, jsonStr);
        } catch (Exception e) {
            logger.error("推送消息失败:topic-[{}],推送内容-[{}],异常信息-[{}]", topic, payload, e.getMessage());
            return new AsyncResult<>(false);
        }
        return new AsyncResult<>(true);
    }

    /**
     * 传入 topic 直接发送
     *
     * @param topic   消息主题
     * @param payload 消息体
     * @return Future
     */
    public <T> Future<Boolean> sendWithTopic(String topic, T payload) {
        try {
            simpMessagingTemplate.convertAndSend(topic, JSONUtil.toJsonStr(payload));
            logger.debug("sendWithTopic >> topic:[{}], payload:[{}] ", topic, payload);
        } catch (Exception e) {
            logger.error("推送消息失败:topic-[{}],推送内容-[{}],异常信息-[{}]", topic, payload, e.getMessage());
            return new AsyncResult<>(false);
        }
        return new AsyncResult<>(true);
    }

}