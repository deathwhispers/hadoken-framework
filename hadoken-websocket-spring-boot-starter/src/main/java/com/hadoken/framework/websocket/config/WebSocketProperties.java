package com.hadoken.framework.websocket.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.Map;

/**
 * @author yanggj
 * @version 1.0.0
 * @date 2022/4/25 15:19
 */
@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "hadoken.websocket")
public class WebSocketProperties implements Serializable {

    private final static long DEFAULT_SERVER_HEART_BEAT = 10000L;
    private final static long DEFAULT_CLIENT_HEART_BEAT = 10000L;

    private final static String DEFAULT_USER_DESTINATION_PREFIX = "/user";
    private final static String[] DEFAULT_APPLICATION_DESTINATION_PREFIX = {"/app"};
    private final static String[] DEFAULT_TOPIC_DESTINATION_PREFIX = {"/topic"};
    private final static String DEFAULT_THREAD_NAME_PREFIX = "hadoken-websocket-";
    private final static int DEFAULT_POOL_SIZE = 1;

    /**
     * STOMP 终端的连接路径（必须）
     * 连接端点，client通过该端点建立连接
     */
    protected String endpoint;
    /**
     * 给指定用户（一对一）发送的主题前缀，默认为 “/user”，唯一
     */
    protected String userDestinationPrefix = DEFAULT_USER_DESTINATION_PREFIX;
    /**
     * 客户端给服务端发消息时的前缀：当客户端给服务端发消息时，需要作为前缀，默认为 “/app”，可配置一个或多个）
     */
    protected String[] applicationDestinationPrefixes = DEFAULT_APPLICATION_DESTINATION_PREFIX;
    /**
     * 允许发到客户端的 topic 前缀，根据业务配置，默认为 “/topic”，可配置一个或多个，不能为空
     */
    protected String[] topicDestinationPrefix = DEFAULT_TOPIC_DESTINATION_PREFIX;
    /**
     * topic 配置项（可选）
     */
    protected Map<String, String> topics;

    protected HeartBeatConfig heartBeat = new HeartBeatConfig();
    protected TaskSchedulerConfig wsTaskScheduler = new TaskSchedulerConfig();

    public String getTopicByKey(String key) {
        return topics.getOrDefault(key, "");
    }

    @Data
    public static class HeartBeatConfig {
        /**
         * 服务端心跳时间，默认 10s
         */
        private Long server = DEFAULT_SERVER_HEART_BEAT;
        /**
         * 客户端心跳时间，默认 10s
         */
        private Long client = DEFAULT_CLIENT_HEART_BEAT;

    }

    @Data
    public static class TaskSchedulerConfig {
        /**
         * 线程名称前缀，默认为 hadoken-websocket-
         */
        private String threadNamePrefix = DEFAULT_THREAD_NAME_PREFIX;
        /**
         * 线程池大小，默认为 1
         */
        private Integer poolSize = DEFAULT_POOL_SIZE;

    }

}
