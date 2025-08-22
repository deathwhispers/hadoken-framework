package com.hadoken.framework.scheduler.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * spring 增强定时任务配置文件
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/14 23:07
 */
@Data
@ConfigurationProperties(prefix = "hadoken.scheduler")
public class HadokenSchedulerProperties {

    /**
     * 是否启用本框架，默认为true
     */
    private boolean enabled = true;

    private int logRetentionSize = 100;

    /**
     * REST管理端点配置
     */
    private Endpoint endpoint = new Endpoint();
    /**
     * 存储策略配置
     */
    private Store store = new Store();
    /**
     * 新增分布式锁的全局配置
     */
    private Lock lock = new Lock();

    @Data
    public static class Endpoint {
        /**
         * 是否启用REST管理端点，默认为false
         */
        private boolean enabled = false;
        /**
         * 端点URL前缀
         */
        private String prefix = "/api/schedule-tasks";
    }

    @Data
    public static class Store {
        public enum Type {MEMORY, JDBC, MYBATIS, REDIS}

        /**
         * 存储类型，默认为内存
         */
        private Type type = Type.MEMORY;

        private JdbcStoreProperties jdbc = new JdbcStoreProperties();
        // Redis存储提供独立的配置
        private RedisStoreProperties redis = new RedisStoreProperties();
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

    @Data
    public static class JdbcStoreProperties {
        /**
         * 任务调度定义表名
         */
        private String definitionTableName = "t_schedule_task_definition";
        /**
         * 任务调度日志表名
         */
        private String logTableName = "t_schedule_task_log";

        private Sql sql = new Sql();

        @Data
        public static class Sql {
            private String save = "INSERT INTO %s (id, description, source_type, bean_name, method_name, trigger_type, trigger_value, status, lock_at_most_for_string) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            private String update = "UPDATE %s SET description = ?, source_type = ?, bean_name = ?, method_name = ?, trigger_type = ?, trigger_value = ?, status = ?, lock_at_most_for_string = ? WHERE id = ?";
            private String updateStatus = "UPDATE %s SET status = ? WHERE id = ?";
            private String findById = "SELECT * FROM %s WHERE id = ?";
            private String findAll = "SELECT * FROM %s";
            private String deleteById = "DELETE FROM %s WHERE id = ?";
        }
    }

    /**
     * Redis存储的专属配置类
     */
    @Data
    public static class RedisStoreProperties {
        /**
         * 存储任务定义的Key的前缀
         */
        private String definitionKeyPrefix = "schedule:task:def:";
        /**
         * 存储所有任务ID的Set集合的Key
         */
        private String allTasksKey = "schedule:tasks:def:";
        /**
         * 日志前缀
         */
        private String logKeyPrefix = "schedule:task:log:";
        private int logRetentionSize = 100;
    }
}