package com.hadoken.framework.mqtt.config;

import com.hadoken.common.exception.BadRequestException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.time.Instant;
import java.util.Collection;

/**
 * @author yanggj
 * @version 1.0.0
 * @date 2023/3/23 9:47
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(MqttConnectProperties.class)
public class MqttAutoConfiguration {

    @Resource
    private MqttConnectProperties mqttConnectProperties;

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(mqttConnectOptions());
        return factory;
    }

    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(mqttConnectProperties.getUsername());
        options.setPassword(mqttConnectProperties.getPassword().toCharArray());
        options.setAutomaticReconnect(true);
        Collection<String> values = mqttConnectProperties.getBrokers();
        if (values.isEmpty()) {
            log.error("未设置mqtt ServerUri");
            throw new BadRequestException("未设置mqtt ServerUri");
        }
        String[] s = new String[values.size()];
        options.setServerURIs(values.toArray(s));


        // 设置CleanSession为true，否则会持久会话，停止服务后会话不会删除，会导致Topic重复订阅
        options.setCleanSession(mqttConnectProperties.getCleanSession());
        options.setAutomaticReconnect(mqttConnectProperties.getAutomaticReconnect());
        return options;
    }

    @Bean
    public MessageChannel inputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(mqttConnectProperties.getClientId(), mqttClientFactory(), mqttConnectProperties.getSub().toArray(new String[0]));
        Collection<Integer> values = mqttConnectProperties.getQos();
        int[] qos = values.stream().mapToInt(item -> item).toArray();
        adapter.setQos(qos);
        adapter.addTopic();
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(messageConverter());
        adapter.setOutputChannel(inputChannel());
        return adapter;
    }

    @Bean
    public MqttMessageConverter messageConverter() {
        return new DefaultPahoMessageConverter();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound(MqttPahoClientFactory mqttClientFactory) {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(
                mqttConnectProperties.getClientId() + "-pub-" + Instant.now().toEpochMilli(), mqttClientFactory);
        messageHandler.setAsync(true);
        messageHandler.setDefaultRetained(false);
        messageHandler.setAsyncEvents(false);
        // Exactly Once
        messageHandler.setDefaultQos(QosConfig.QOS_2);
        return messageHandler;
    }
}
