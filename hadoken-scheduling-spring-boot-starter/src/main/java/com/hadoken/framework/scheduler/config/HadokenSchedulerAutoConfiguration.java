package com.hadoken.framework.scheduler.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hadoken.framework.scheduler.endpoint.SchedulerController;
import com.hadoken.framework.scheduler.endpoint.SchedulerLogController;
import com.hadoken.framework.scheduler.lock.DistributedLockProvider;
import com.hadoken.framework.scheduler.manager.TaskManager;
import com.hadoken.framework.scheduler.manager.TaskManagerImpl;
import com.hadoken.framework.scheduler.store.TaskLogStore;
import com.hadoken.framework.scheduler.store.TaskStore;
import com.hadoken.framework.scheduler.store.memory.InMemoryTaskLogStore;
import com.hadoken.framework.scheduler.store.memory.InMemoryTaskStore;
import com.hadoken.framework.scheduler.store.mybatis.MybatisTaskLogStore;
import com.hadoken.framework.scheduler.store.mybatis.MybatisTaskStore;
import com.hadoken.framework.scheduler.store.mybatis.TaskDefinitionMapper;
import com.hadoken.framework.scheduler.store.mybatis.TaskLogMapper;
import com.hadoken.framework.scheduler.store.redis.RedisTaskLogStore;
import com.hadoken.framework.scheduler.store.redis.RedisTaskStore;
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
import org.springframework.data.redis.core.StringRedisTemplate;
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
     * 根据配置动态创建唯一的TaskStore Bean。
     * 使用ObjectProvider来安全地、懒加载地获取不同实现所需的依赖。
     */
    @Bean
    @ConditionalOnMissingBean(TaskStore.class)
    public TaskStore taskStore(
            HadokenSchedulerProperties properties,
            ObjectProvider<TaskDefinitionMapper> mybatisMapperProvider,
            ObjectProvider<StringRedisTemplate> redisTemplateProvider,
            ObjectProvider<ObjectMapper> objectMapperProvider
    ) {
        HadokenSchedulerProperties.Store.Type type = properties.getStore().getType();
        log.info(">>> 使用 {} 方式配置定时任务存储", type);
        return switch (type) {
            case MYBATIS -> new MybatisTaskStore(mybatisMapperProvider.getIfAvailable());
            case REDIS -> new RedisTaskStore(
                    redisTemplateProvider.getIfAvailable(),
                    properties.getStore().getRedis(),
                    objectMapperProvider.getIfAvailable(ObjectMapper::new)
            );
            case MEMORY -> {
                log.warn(">>> 任务管理器正在使用内存做为任务存储方式.");
                yield new InMemoryTaskStore();
            }
        };
    }

    /**
     * 根据配置动态创建唯一的TaskLogStore Bean。
     */
    @Bean
    @ConditionalOnMissingBean(TaskLogStore.class)
    public TaskLogStore taskLogStore(
            HadokenSchedulerProperties properties,
            ObjectProvider<TaskLogMapper> mybatisLogMapperProvider,
            ObjectProvider<StringRedisTemplate> redisTemplateProvider,
            ObjectProvider<ObjectMapper> objectMapperProvider
    ) {
        HadokenSchedulerProperties.Store.Type type = properties.getStore().getType();
        log.info(">>> 使用 {} 方式配置 日志存储", type);
        return switch (type) {
            case MYBATIS -> new MybatisTaskLogStore(mybatisLogMapperProvider.getIfAvailable());
            case REDIS -> new RedisTaskLogStore(
                    redisTemplateProvider.getIfAvailable(),
                    properties.getStore().getRedis(),
                    objectMapperProvider.getIfAvailable(ObjectMapper::new));
            case MEMORY -> {
                log.warn(">>> 任务管理器正在使用内存做为日志存储方式.");
                yield new InMemoryTaskLogStore();
            }
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
     * 将Endpoint的配置拆分为两个独立的Bean，职责更清晰
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
        public SchedulerController schedulerController(TaskManager taskManager) {
            return new SchedulerController(taskManager);
        }

        @Bean
        @ConditionalOnMissingBean
        public SchedulerLogController schedulerLogController(TaskLogStore taskLogStore, HadokenSchedulerProperties properties) {
            return new SchedulerLogController(taskLogStore, properties);
        }
    }

}