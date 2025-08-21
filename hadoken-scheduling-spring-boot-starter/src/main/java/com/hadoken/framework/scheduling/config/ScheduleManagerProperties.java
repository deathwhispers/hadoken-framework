package com.hadoken.framework.scheduling.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/14 23:07
 */
@Data
@ConfigurationProperties(prefix = "schedule.manager")
public class ScheduleManagerProperties {

    /**
     * 是否启用本框架，默认为true
     */
    private boolean enabled = true;

    private int logRetentionSize = 100;

    /**
     * 存储策略配置
     */
    private Store store = new Store();

    /**
     * REST管理端点配置
     */
    private Endpoint endpoint = new Endpoint();

    /**
     * 新增分布式锁的全局配置
     */
    private Lock lock = new Lock();

    @Data
    public static class Store {
        public enum Type {MEMORY, JDBC, REDIS}

        /**
         * 存储类型，默认为内存
         */
        private Type type = Type.MEMORY;
    }

    @Data
    public static class Endpoint {
        /**
         * 是否启用REST管理端点，默认为false
         */
        private boolean enabled = false;
        /**
         * 端点URL前缀
         */
        private String basePath = "/api/schedule-tasks";
    }

    @Data
    public static class Lock {
        /**
         * 是否默认启用分布式锁。
         * 如果设置为true，所有未显式配置 lockAtMostForString 的任务也会尝试获取锁。
         */
        private boolean enabled = false;

        /**
         * 全局默认的锁最长持有时间。
         * 遵循java.time.Duration格式, e.g., "PT5M", "PT30S"。
         * 当任务注解中未指定 lockAtMostForString 时，此配置生效。
         */
        private String defaultAtMostFor = "PT5M"; // 默认5分钟
    }
}