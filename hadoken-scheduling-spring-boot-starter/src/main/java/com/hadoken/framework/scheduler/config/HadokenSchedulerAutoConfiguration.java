package com.hadoken.framework.scheduler.config;

import com.hadoken.framework.scheduler.endpoint.SchedulerController;
import com.hadoken.framework.scheduler.endpoint.SchedulerLogController;
import com.hadoken.framework.scheduler.lock.DistributedLockProvider;
import com.hadoken.framework.scheduler.manager.TaskManager;
import com.hadoken.framework.scheduler.manager.TaskManagerImpl;
import com.hadoken.framework.scheduler.store.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.RestController;

/**
 * 轻量级调度器的核心自动配置类。
 * 它会在满足条件时，自动为Spring容器配置好框架所需的所有核心组件。
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/13 11:22
 */
@Slf4j
@ConditionalOnProperty(name = "hadoken.scheduler.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({HadokenSchedulerProperties.class, TaskSchedulingProperties.class})
@Import({HadokenSchedulerConfigurer.class})
public class HadokenSchedulerAutoConfiguration {

    private final HadokenSchedulerProperties properties;

    public HadokenSchedulerAutoConfiguration(HadokenSchedulerProperties properties) {
        this.properties = properties;
    }

    /**
     * 定义默认的TaskStore Bean (内存实现)。
     * <p>
     * {@link ConditionalOnMissingBean} 是关键：如果用户在自己的配置中定义了任何
     * 一个 {@link TaskStore} 类型的 Bean（无论是 JPA 还是 MyBatis 实现），那么这个默认的Bean就不会被创建。
     * 这赋予了用户完全的持久化控制权。
     */
    @Bean
    @ConditionalOnMissingBean(TaskStore.class)
    public TaskStore taskStore() {
        log.warn(">>> 未找到持久化的任务存储（TaskStore）Bean。回退到默认的内存任务存储（InMemoryTaskStore）。" +
                "应用程序重启时，任务定义和状态将会丢失。");
        HadokenSchedulerProperties.Store.Type type = properties.getStore().getType();
        return switch (type) {
            case MEMORY -> new InMemoryTaskStore();
            // todo
            case JDBC -> new JdbcTaskStore(null);
//            case REDIS -> new RedisTaskStore();
            default -> new InMemoryTaskStore();
        };
    }

    @Bean
    @ConditionalOnMissingBean(TaskLogStore.class)
    public TaskLogStore taskLogStore() {
        HadokenSchedulerProperties.Store.Type type = properties.getStore().getType();
        return switch (type) {
            case MEMORY -> new InMemoryTaskLogStore();
            default -> new InMemoryTaskLogStore();
        };
    }

    /**
     * 定义核心的 TaskManager。
     * 它依赖于 Spring 容器中已经存在的全局 TaskScheduler 和我们定义的 TaskStore
     * 注意：我们直接注入 ThreadPoolTaskScheduler，这是 Spring Boot 在 TaskSchedulingAutoConfiguration 中
     * 默认创建的Bean。我们复用它，而不是创建新的，以保证配置统一。
     */
    @Bean
    @ConditionalOnMissingBean(TaskManager.class)
    public TaskManager taskManager(ThreadPoolTaskScheduler taskScheduler, TaskStore taskStore, TaskLogStore taskLogStore,
                                   ApplicationContext applicationContext,
                                   ObjectProvider<DistributedLockProvider> lockProvider) {
        return new TaskManagerImpl(taskScheduler, taskStore, taskLogStore, this.properties, applicationContext, lockProvider);
    }

    /**
     * [重构] 将Endpoint的配置拆分为两个独立的Bean，职责更清晰
     * WebApi 自动配置，满足条件时生效
     * 只有 web servlet 容器时才创建
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnClass(RestController.class)
    @ConditionalOnProperty(name = "hadoken.scheduler.endpoint.enabled", havingValue = "true", matchIfMissing = true)
    static class EndpointsConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public SchedulerController schedulerManagementController(TaskManager taskManager) {
            return new SchedulerController(taskManager);
        }

        @Bean
        @ConditionalOnMissingBean
        public SchedulerLogController scheduleLogController(TaskLogStore taskLogStore, HadokenSchedulerProperties properties) {
            return new SchedulerLogController(taskLogStore, properties);
        }
    }

}