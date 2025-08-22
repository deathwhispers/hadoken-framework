package com.hadoken.framework.scheduling.manager;

import com.hadoken.framework.scheduling.config.HadokenSchedulerProperties;
import com.hadoken.framework.scheduling.enums.TaskSourceType;
import com.hadoken.framework.scheduling.enums.TaskStatus;
import com.hadoken.framework.scheduling.lock.DistributedLockProvider;
import com.hadoken.framework.scheduling.model.ManagedTask;
import com.hadoken.framework.scheduling.model.TaskDefinition;
import com.hadoken.framework.scheduling.store.TaskStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/13 11:20
 */
@Slf4j
public class TaskManagerImpl implements TaskManager {

    private final Map<String, ManagedTask> runtimeTasks = new ConcurrentHashMap<>();

    private final ThreadPoolTaskScheduler taskScheduler;
    private final TaskStore taskStore;
    private final HadokenSchedulerProperties properties;
    private final ApplicationContext applicationContext;
    private final String instanceId;
    private final Optional<DistributedLockProvider> lockProviderOpt;
    // 运行时的任务实例缓存

    public TaskManagerImpl(ThreadPoolTaskScheduler taskScheduler, TaskStore taskStore,
                           HadokenSchedulerProperties properties, ApplicationContext applicationContext,
                           ObjectProvider<DistributedLockProvider> lockProvider) {
        this.taskScheduler = taskScheduler;
        this.taskStore = taskStore;
        this.properties = properties;
        this.applicationContext = applicationContext;
        this.instanceId = generateInstanceId();
        this.lockProviderOpt = Optional.ofNullable(lockProvider.getIfAvailable()); // 安全地获取Bean
        if (this.lockProviderOpt.isPresent()) {
            log.info("已找到分布式锁提供程序（DistributedLockProvider）Bean。分布式锁功能已启用。");
        }
    }

    // 【核心修正】: 移除 @PostConstruct 注解
    // 初始化逻辑将由 Spring 的 @EnableScheduling -> SchedulingConfigurer -> registerAndSync 流程驱动，
    // 以及一个显式的应用启动监听器来完成。
    @Override
    @EventListener(ApplicationReadyEvent.class) // [修正] 确保在应用完全就绪后执行
    public void initializeTasks() {
        log.info("正在从任务存储（TaskStore）中为实例初始化任务 {}...", this.instanceId);
        taskStore.findAll().forEach(definition -> {
            try {
                // 只加载动态创建的任务，注解任务由Configurer流程处理
                if (definition.getSourceType() == TaskSourceType.DYNAMIC) {
                    ManagedTask managedTask = resolveTaskDefinition(definition);
                    runtimeTasks.put(definition.getId(), managedTask);
                    if (definition.getStatus() == TaskStatus.RUNNING) {
                        log.info("由于持久化状态为运行中，自动启动动态任务 {} 。", definition.getId());
                        start(definition.getId());
                    }
                }
            } catch (Exception e) {
                log.error("无法从存储中初始化任务 {} 。原因：{}", definition.getId(), e.getMessage(), e);
            }
        });
        log.info("已完成{}个任务的初始化。", runtimeTasks.size());
    }


    @Override
    public Runnable registerAndSync(TaskDefinition definition, Runnable originalRunnable) {
        // 同步逻辑：以代码中的定义为准，更新数据库
        Optional<TaskDefinition> existingOpt = taskStore.findById(definition.getId());
        TaskDefinition definitionToUse;

        if (existingOpt.isPresent()) {
            TaskDefinition dbDefinition = existingOpt.get();
            // 代码中的定义覆盖数据库，但保留数据库中的状态
            definition.setStatus(dbDefinition.getStatus());
            taskStore.update(definition);
            definitionToUse = definition;
            log.info("存储中 {} 的任务定义已更新。", definition.getId());
        } else {
            // 新任务默认为运行状态，因为@Scheduled注解的任务默认就是启用的
            definition.setStatus(TaskStatus.RUNNING);
            // 如果数据库中没有，则保存新的任务定义
            taskStore.save(definition);
            log.info(" {} 的新任务定义已保存到存储中。", definition.getId());
            definitionToUse = definition;
        }

        // 创建运行时任务对象
        ManagedTask managedTask = new ManagedTask(definitionToUse,
                originalRunnable, this.instanceId,
                properties.getLogRetentionSize(),
                this.lockProviderOpt
        );
        runtimeTasks.put(definition.getId(), managedTask);

        // 【核心修正】: 根据持久化的状态决定是否在注册时就启动
        if (definitionToUse.getStatus() == TaskStatus.RUNNING) {
            // 注意：我们不能在这里直接调用 start()，因为调度器此时可能还在配置中。
            // Spring 的 SchedulingConfigurer 会处理启动。
            log.info("任务 {} 被标记为正在运行。它将被安排运行。", definition.getId());
        } else {
            log.info("任务 {} 被标记为已停止。它将被注册，但不会被调度。", definition.getId());
        }

        // 返回包装后的runnable，但只有在状态为RUNNING时才真正执行
        return () -> {
            ManagedTask currentTask = runtimeTasks.get(definition.getId());
            if (currentTask != null && currentTask.getDefinition().getStatus() == TaskStatus.RUNNING) {
                managedTask.getRunnable().run();
            }
        };
    }


    @Override
    public void processAnnotatedTask(TaskDefinition definition, Runnable originalRunnable, Trigger trigger) {
        Optional<TaskDefinition> existingOpt = taskStore.findById(definition.getId());
        TaskDefinition definitionToUse;

        if (existingOpt.isPresent()) {
            TaskDefinition dbDefinition = existingOpt.get();
            definition.setStatus(dbDefinition.getStatus()); // 保持数据库中存储的状态
            if (!isDefinitionEqual(definition, dbDefinition)) {
                taskStore.update(definition);
                log.info("存储中 {} 的任务定义已更新。", definition.getId());
            }
            definitionToUse = definition;
        } else {
            definition.setStatus(TaskStatus.RUNNING); // 注解任务默认就是运行状态
            taskStore.save(definition);
            log.info(" {} 的新任务定义已保存至存储。", definition.getId());
            definitionToUse = definition;
        }

        ManagedTask managedTask = new ManagedTask(definitionToUse,
                originalRunnable, this.instanceId,
                properties.getLogRetentionSize(),
                this.lockProviderOpt
        );
        runtimeTasks.put(definition.getId(), managedTask);

        // [修正] 如果任务状态是RUNNING，则立即调度并保存Future
        if (definitionToUse.getStatus() == TaskStatus.RUNNING) {
            ScheduledFuture<?> future = taskScheduler.schedule(managedTask.getRunnable(), trigger);
            managedTask.setScheduledFuture(future);
            log.info("任务 {} 已被安排运行。", definition.getId());
        } else {
            log.info("任务 {} 已注册但处于停止状态，不会被调度。", definition.getId());
        }
    }

    @Override
    public void start(String taskId) {
        ManagedTask task = getTaskOrThrow(taskId);
        synchronized (task) {
            if (task.getStatus() == TaskStatus.RUNNING && task.getScheduledFuture() != null && !task.getScheduledFuture().isDone()) {
                log.warn("任务 '{}' 已在运行中.", taskId);
                return;
            }

            task.getDefinition().setStatus(TaskStatus.RUNNING);
            taskStore.updateStatus(taskId, TaskStatus.RUNNING);

            Trigger trigger = buildTriggerFromDefinition(task.getDefinition());
            ScheduledFuture<?> future = taskScheduler.schedule(task.getRunnable(), trigger);

            task.setScheduledFuture(future);
            log.info("任务 '{}' 启动.", taskId);
        }
    }

    @Override
    public void stop(String taskId) {
        ManagedTask task = getTaskOrThrow(taskId);
        synchronized (task) {
            ScheduledFuture<?> future = task.getScheduledFuture();
            if (future != null) {
                // false: 不要中断正在执行的任务
                boolean cancelled = future.cancel(false);
                if (cancelled) {
                    task.setScheduledFuture(null);
                    task.getDefinition().setStatus(TaskStatus.STOPPED);
                    taskStore.updateStatus(taskId, TaskStatus.STOPPED);
                    log.info("任务 '{}' 已成功停止.", taskId);
                } else {
                    log.warn("无法停止任务 '{}'。该任务可能当前正在运行且无法被中断。", taskId);
                }
            } else {
                // 如果future为null，但状态是RUNNING，说明可能是一个还未被调度的任务，直接更新状态
                if (task.getDefinition().getStatus() == TaskStatus.RUNNING) {
                    task.getDefinition().setStatus(TaskStatus.STOPPED);
                    taskStore.updateStatus(taskId, TaskStatus.STOPPED);
                    log.info("任务 '{}' 未被调度，已标记为停止。", taskId);
                }
            }
        }
    }

    @Override
    public void triggerOnce(String taskId) {
        ManagedTask task = getTaskOrThrow(taskId);
        log.info("触发任务 '{}' 立即进行单次执行。", taskId);
        taskScheduler.schedule(task.getRunnable(), Instant.now());
    }

    @Override
    public Optional<ManagedTask> getTask(String taskId) {
        return Optional.ofNullable(runtimeTasks.get(taskId));
    }

    @Override
    public Collection<ManagedTask> getAllTasks() {
        return List.copyOf(runtimeTasks.values());
    }

    @Override
    public void createDynamicTask(TaskDefinition definition) {
        log.info("尝试创建一个ID为 '{}' 的新动态任务。", definition.getId());

        // 1. 校验ID是否已存在
        if (taskStore.findById(definition.getId()).isPresent() || runtimeTasks.containsKey(definition.getId())) {
            throw new IllegalStateException("任务 id '" + definition.getId() + "' 已经存在.");
        }
        // 对 beanName + methodName 进行唯一性校验
        boolean alreadyExists = runtimeTasks.values().stream()
                .map(ManagedTask::getDefinition)
                .anyMatch(def -> def.getBeanName().equals(definition.getBeanName()) &&
                        def.getMethodName().equals(definition.getMethodName()));

        if (alreadyExists) {
            throw new IllegalStateException("在 bean '" + definition.getMethodName() + "' 上，针对方法 '" +
                    definition.getBeanName() + "' 的任务已注册.");
        }

        // 2. 标记任务来源为动态，并设置初始状态为运行中
        definition.setSourceType(TaskSourceType.DYNAMIC);
        definition.setStatus(TaskStatus.RUNNING); // 动态创建的任务默认为直接运行

        // 3. 预解析验证：在持久化之前，先尝试解析Bean和Method，确保它是有效的。
        ManagedTask managedTask = resolveTaskDefinition(definition);

        // 4. 持久化任务定义
        taskStore.save(definition);
        log.info("动态任务定义 '{}' 已保存到存储中。", definition.getId());

        // 5. 加入运行时缓存并启动
        runtimeTasks.put(definition.getId(), managedTask);
        start(definition.getId());

        log.info("动态任务 '{}' 创建并成功启动。", definition.getId());
    }

    @Override
    public void deleteTask(String taskId) {
        log.info("尝试删除任务 '{}' 。", taskId);

        // 1. 停止并移除运行时的任务
        // getTaskOrThrow 会确保任务在运行时存在，如果不存在会抛异常
        stop(taskId);
        runtimeTasks.remove(taskId);

        // 2. 从持久化存储中删除
        taskStore.deleteById(taskId);

        log.info("任务 '{}' 已从运行时和存储中成功删除。", taskId);
    }


    /**
     * 内部帮助方法，获取任务，如果不存在则抛出异常。
     *
     * @param taskId 任务ID
     * @return 找到的ManagedTask实例
     * @throws IllegalArgumentException 如果任务不存在
     */
    private ManagedTask getTaskOrThrow(String taskId) {
        ManagedTask task = runtimeTasks.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("在运行时注册表中未找到ID为 '" + taskId + "' 的任务。");
        }
        return task;
    }

    private ManagedTask resolveTaskDefinition(TaskDefinition definition) {
        if (!StringUtils.hasText(definition.getBeanName()) || !StringUtils.hasText(definition.getMethodName())) {
            throw new IllegalStateException("任务 '" + definition.getId() + "' 缺失 bean name 或 method name.");
        }
        try {
            Object bean = applicationContext.getBean(definition.getBeanName());

            Method method = ReflectionUtils.findMethod(bean.getClass(), definition.getMethodName());

            if (method == null) {
                throw new NoSuchMethodException("'没有在类上 '" + definition.getBeanName() + "' 找到方法 '" + definition.getMethodName() + "'");
            }
            if (method.getParameterCount() > 0) {
                throw new IllegalArgumentException("计划任务方法'" + definition.getMethodName() + "' 不能有参数。");
            }
            ReflectionUtils.makeAccessible(method);
            Runnable runnable = () -> {
                try {
                    method.invoke(bean);
                } catch (Exception e) {
                    throw new RuntimeException("未能执行已解析的任务方法，任务为：" + definition.getId(), e);
                }
            };
            return new ManagedTask(definition, runnable, this.instanceId, properties.getLogRetentionSize(), this.lockProviderOpt);
        } catch (Exception e) {
            throw new IllegalStateException("无法解析任务的 bean/method '" + definition.getId() + "'", e);
        }
    }

    /**
     * 根据TaskDefinition构建Spring的Trigger对象。
     *
     * @param definition 任务定义
     * @return Spring的Trigger实例
     */
    private Trigger buildTriggerFromDefinition(TaskDefinition definition) {
        return switch (definition.getTriggerType()) {
            case CRON -> new CronTrigger(definition.getTriggerValue());
            case FIXED_RATE -> {
                PeriodicTrigger trigger = new PeriodicTrigger(parseDuration(definition.getTriggerValue()));
                trigger.setFixedRate(true);
                yield trigger;
            }
            case FIXED_DELAY -> {
                PeriodicTrigger trigger = new PeriodicTrigger(parseDuration(definition.getTriggerValue()));
                trigger.setFixedRate(false);
                yield trigger;
            }
        };
    }

    /**
     * 使用Spring Boot的工具类解析时间格式字符串（如 "5s", "1m"）。
     *
     * @param durationString 时间字符串
     * @return Duration 对象
     */
    private Duration parseDuration(String durationString) {
        try {
            // 移除毫秒的"ms"后缀，因为 Duration Style 不支持
            if (durationString.endsWith("ms")) {
                long millis = Long.parseLong(durationString.substring(0, durationString.length() - 2).trim());
                return Duration.ofMillis(millis);
            }
            return DurationStyle.detectAndParse(durationString);
        } catch (Exception e) {
            throw new IllegalArgumentException("无效的时长字符串格式：'" + durationString + "'", e);
        }
    }

    private boolean isDefinitionEqual(TaskDefinition codeDef, TaskDefinition dbDef) {
        return codeDef.getDescription().equals(dbDef.getDescription()) &&
                codeDef.getBeanName().equals(dbDef.getBeanName()) &&
                codeDef.getMethodName().equals(dbDef.getMethodName()) &&
                codeDef.getTriggerType() == dbDef.getTriggerType() &&
                codeDef.getTriggerValue().equals(dbDef.getTriggerValue());
    }

    private String generateInstanceId() {
        try {
            return InetAddress.getLocalHost().getHostName() + "-" + UUID.randomUUID().toString().substring(0, 8);
        } catch (UnknownHostException e) {
            log.warn("无法确定主机名。回退到使用UUID作为实例ID。.", e);
            return "unknown-host-" + UUID.randomUUID().toString().substring(0, 8);
        }
    }
}