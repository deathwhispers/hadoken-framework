package com.hadoken.framework.websocket.core.message;

import java.util.concurrent.Future;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/5 11:46
 */
public interface WebSocketSender {

    <T> Future<Boolean> sendByKey(String key, T payload);

    <T> Future<Boolean> sendByKey(String key, T payload, boolean ignoreNull);

    Future<Boolean> sendByKey(String key, String payload);

    <T> Future<Boolean> sendWithTopic(String topic, T payload);

    <T> Future<Boolean> sendWithTopic(String topic, T payload, boolean ignoreNull);

    Future<Boolean> sendWithTopic(String topic, String payload);

}
