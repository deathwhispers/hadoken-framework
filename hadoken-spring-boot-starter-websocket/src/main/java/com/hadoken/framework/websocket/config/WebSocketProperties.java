package com.hadoken.framework.websocket.config;

import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.Map;

/**
 * @author yanggj
 * @version 1.0.0
 * @date 2022/4/25 15:19
 */
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

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String[] getApplicationDestinationPrefixes() {
        return applicationDestinationPrefixes;
    }

    public void setApplicationDestinationPrefixes(String... applicationDestinationPrefixes) {
        this.applicationDestinationPrefixes = applicationDestinationPrefixes;
    }

    public String getUserDestinationPrefix() {
        return userDestinationPrefix;
    }

    public void setUserDestinationPrefix(String userDestinationPrefix) {
        this.userDestinationPrefix = userDestinationPrefix;
    }

    public String[] getTopicDestinationPrefix() {
        return topicDestinationPrefix;
    }

    public void setTopicDestinationPrefix(String[] topicDestinationPrefix) {
        this.topicDestinationPrefix = topicDestinationPrefix;
    }

    public Map<String, String> getTopics() {
        return topics;
    }

    public void setTopics(Map<String, String> topics) {
        this.topics = topics;
    }

    public HeartBeatConfig getHeartBeat() {
        return heartBeat;
    }

    public void setHeartBeat(HeartBeatConfig heartBeat) {
        this.heartBeat = heartBeat;
    }

    public TaskSchedulerConfig getWsTaskScheduler() {
        return wsTaskScheduler;
    }

    public void setWsTaskScheduler(TaskSchedulerConfig wsTaskScheduler) {
        this.wsTaskScheduler = wsTaskScheduler;
    }

    @ToString
    public static class HeartBeatConfig {
        /**
         * 服务端心跳时间，默认 10s
         */
        private Long server = DEFAULT_SERVER_HEART_BEAT;
        /**
         * 客户端心跳时间，默认 10s
         */
        private Long client = DEFAULT_CLIENT_HEART_BEAT;

        public Long getServer() {
            return server;
        }

        public void setServer(Long server) {
            this.server = server;
        }

        public Long getClient() {
            return client;
        }

        public void setClient(Long client) {
            this.client = client;
        }
    }

    @ToString
    public static class TaskSchedulerConfig {
        /**
         * 线程名称前缀，默认为 hadoken-websocket-
         */
        private String threadNamePrefix = DEFAULT_THREAD_NAME_PREFIX;
        /**
         * 线程池大小，默认为 1
         */
        private Integer poolSize = DEFAULT_POOL_SIZE;

        public String getThreadNamePrefix() {
            return threadNamePrefix;
        }

        public void setThreadNamePrefix(String threadNamePrefix) {
            this.threadNamePrefix = threadNamePrefix;
        }

        public Integer getPoolSize() {
            return poolSize;
        }

        public void setPoolSize(Integer poolSize) {
            this.poolSize = poolSize;
        }
    }

}
