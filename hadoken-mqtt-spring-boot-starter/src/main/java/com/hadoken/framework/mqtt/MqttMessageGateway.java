package com.hadoken.framework.mqtt;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * @author yanggj
 * @version 1.0.0
 * @date 2023/4/13 17:38
 */
@Component
@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttMessageGateway {
    
    void send(String payload);

    void sendToTopic(String payload, @Header(MqttHeaders.TOPIC) String topic);

    void sendToTopic(String payload, @Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos);
}
