package com.hadoken.framework.mqtt.config;

import cn.hutool.core.util.RandomUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.List;

/**
 * @author yanggj
 * @version 1.0.0
 * @date 2023/3/22 16:19
 */
@Data
@ConfigurationProperties(prefix = MqttConnectProperties.PREFIX)
public class MqttConnectProperties implements Serializable {

    public static final String PREFIX = "hadoken.mqtt";

    private final static String DEFAULT_CLIENT_ID = RandomUtil.randomString(16);
    private final static boolean DEFAULT_AUTOMATIC_RECONNECT = true;
    private final static boolean DEFAULT_CLEAN_SESSION = true;

    protected Integer maxInflight;
    protected String clientId = DEFAULT_CLIENT_ID;
    protected String username;
    protected String password;
    protected List<String> brokers;
    protected Boolean automaticReconnect = DEFAULT_AUTOMATIC_RECONNECT;
    protected Boolean cleanSession = DEFAULT_CLEAN_SESSION;
    protected String connectionTimeout;
    protected List<String> sub;
    protected List<String> pub;
    protected List<Integer> qos;

}
