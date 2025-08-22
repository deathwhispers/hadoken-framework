package com.hadoken.framework.scheduling.config;

import com.hadoken.framework.scheduling.annotation.EnhanceScheduled;
import com.hadoken.framework.scheduling.enums.TaskSourceType;
import com.hadoken.framework.scheduling.enums.TaskStatus;
import com.hadoken.framework.scheduling.enums.TriggerType;
import com.hadoken.framework.scheduling.manager.TaskManager;
import com.hadoken.framework.scheduling.model.TaskDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.IntervalTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.*;

/**
 * 实现了SchedulingConfigurer的配置类。
 * 职责：作为Spring调度的“挂钩”，拦截、包装并重新注册所有@Scheduled任务。
 * 它清晰地依赖于TaskManager，由Spring容器保证注入顺序，从而打破循环依赖。
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/14 21:19
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class HadokenSchedulerConfigurer implements SchedulingConfigurer {

    private final TaskManager taskManager;
    private final ApplicationContext applicationContext;
    private final HadokenSchedulerProperties properties;

    public HadokenSchedulerConfigurer(TaskManager taskManager,
                                      ApplicationContext applicationContext,
                                      HadokenSchedulerProperties properties) {
        this.taskManager = taskManager;
        this.applicationContext = applicationContext;
        this.properties = properties;
    }

    /**
     * 这是SchedulingConfigurer接口要求实现的方法。
     * Spring在处理完所有@Scheduled注解后，会调用此方法，并将包含所有已发现任务的registrar传递给我们。
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // 复制Spring发现的所有任务
        List<TaskHolder> allTasks = new ArrayList<>();
        allTasks.addAll(taskRegistrar.getTriggerTaskList().stream().map(TaskHolder::new).toList());
        allTasks.addAll(taskRegistrar.getFixedRateTaskList().stream().map(t -> new TaskHolder(t, true)).toList());
        allTasks.addAll(taskRegistrar.getFixedDelayTaskList().stream().map(t -> new TaskHolder(t, false)).toList());
        // 【核心修正】: 增加ID唯一性校验
        validateTaskIds(allTasks);

        Set<Method> processedMethods = new HashSet<>();
        clearTasksFromRegistrar(taskRegistrar);
        // 清空Spring的原始任务列表。
        // 直接调用 clear() 会因列表不可修改而抛出异常。
        // 我们通过反射访问并清空底层的可修改列表，以防止任务被重复调度。
        clearTasksFromRegistrar(taskRegistrar);

        allTasks.forEach(taskHolder -> {
            if (shouldProcess(taskHolder.getRunnable(), processedMethods)) {
                taskManager.processAnnotatedTask(
                        buildTaskDefinition(taskHolder.getRunnable(), taskHolder.getTrigger()),
                        taskHolder.getRunnable(),
                        taskHolder.getTrigger()
                );
            }
        });
    }

    /**
     * 校验所有已发现的注解任务，确保ID的唯一性。
     */
    private void validateTaskIds(List<TaskHolder> allTasks) {
        Map<String, String> idToMethodSignature = new HashMap<>();

        for (TaskHolder task : allTasks) {
            if (task.getRunnable() instanceof ScheduledMethodRunnable smr) {
                Method method = smr.getMethod();
                EnhanceScheduled ann = AnnotationUtils.findAnnotation(method, EnhanceScheduled.class);
                if (ann != null) {
                    String taskId = ann.id();
                    String methodSignature = method.getDeclaringClass().getName() + "#" + method.getName();

                    if (idToMethodSignature.containsKey(taskId)) {
                        String existingMethod = idToMethodSignature.get(taskId);
                        throw new IllegalStateException(String.format(
                                "发现重复的任务ID '%s'，该ID同时被 '%s' 和 '%s' 使用，任务ID必须唯一。",
                                taskId, existingMethod, methodSignature
                        ));
                    }
                    idToMethodSignature.put(taskId, methodSignature);
                }
            }
        }
    }

    private boolean shouldProcess(Runnable runnable, Set<Method> processedMethods) {
        if (runnable instanceof ScheduledMethodRunnable smr) {
            // 如果方法已经被处理过，则跳过
            return processedMethods.add(smr.getMethod());
        }
        return true; // 总是处理非方法任务 (例如 lambda)
    }

    private TaskDefinition buildTaskDefinition(Runnable runnable, Trigger trigger) {
        TaskDefinition.TaskDefinitionBuilder builder = TaskDefinition.builder();
        if (runnable instanceof ScheduledMethodRunnable smr) {
            Method method = smr.getMethod();
            Object targetBean = smr.getTarget();
            String beanName = findBeanName(AopUtils.getTargetClass(targetBean));
            builder.beanName(beanName).methodName(method.getName());
            EnhanceScheduled ann = AnnotationUtils.findAnnotation(method, EnhanceScheduled.class);
            if (ann != null) {
                builder.id(ann.id()).description(ann.description());

                if (StringUtils.hasText(ann.lockAtMostForString())) {
                    builder.lockAtMostForString(ann.lockAtMostForString());
                } else if (properties.getLock().isEnabled()) {
                    // 如果注解中未配置，但全局锁已启用，则使用全局默认值
                    builder.lockAtMostForString(properties.getLock().getDefaultAtMostFor());
                }

            } else {
                builder.id(beanName + "#" + method.getName());
                builder.description(" Spring 原生定时任务，非自定义注解 @EnhanceScheduled");
                // 标准 @Scheduled 任务也支持全局默认锁
                if (properties.getLock().isEnabled()) {
                    builder.lockAtMostForString(properties.getLock().getDefaultAtMostFor());
                }
            }
        } else {
            builder.id("dynamic-lambda-task-" + runnable.hashCode());
            builder.description("已提交的 lambda 动态任务");
        }
        if (trigger instanceof CronTrigger ct) {
            builder.triggerType(TriggerType.CRON).triggerValue(ct.getExpression());
        } else if (trigger instanceof PeriodicTrigger pt) {
            Duration period = pt.getPeriodDuration();
            builder.triggerType(pt.isFixedRate() ? TriggerType.FIXED_RATE : TriggerType.FIXED_DELAY)
                    .triggerValue(period.toMillis() + "ms");
        }
        return builder.status(TaskStatus.STOPPED)
                .sourceType(TaskSourceType.ANNOTATED)
                .build();
    }

    private String findBeanName(Class<?> beanClass) {
        try {
            String[] beanNames = this.applicationContext.getBeanNamesForType(beanClass);
            if (beanNames.length > 0) return beanNames[0];
        } catch (NoSuchBeanDefinitionException e) { /* ignore */ }
        return beanClass.getName();
    }

    /**
     * 通过反射清空ScheduledTaskRegistrar内部的任务列表。
     * 这是必需的，因为其getter方法返回的是不可修改的列表。
     *
     * @param registrar 任务注册器
     */
    private void clearTasksFromRegistrar(ScheduledTaskRegistrar registrar) {
        clearTaskList(registrar, "triggerTasks");
        clearTaskList(registrar, "fixedRateTasks");
        clearTaskList(registrar, "fixedDelayTasks");
    }

    private void clearTaskList(ScheduledTaskRegistrar registrar, String fieldName) {
        try {
            Field field = ReflectionUtils.findField(ScheduledTaskRegistrar.class, fieldName);
            if (field != null) {
                ReflectionUtils.makeAccessible(field);
                Object taskList = field.get(registrar);
                if (taskList instanceof List) {
                    ((List<?>) taskList).clear();
                }
            }
        } catch (IllegalAccessException e) {
            log.warn("无法通过反射清空任务列表 '{}'。调度任务可能会重复。", fieldName, e);
        }
    }

    /**
     * 内部辅助类，用于统一处理不同类型的任务
     */
    private static class TaskHolder {
        private final Runnable runnable;
        private final Trigger trigger;

        TaskHolder(TriggerTask triggerTask) {
            this.runnable = triggerTask.getRunnable();
            this.trigger = triggerTask.getTrigger();
        }

        TaskHolder(IntervalTask intervalTask, boolean isFixedRate) {
            this.runnable = intervalTask.getRunnable();
            PeriodicTrigger periodicTrigger = new PeriodicTrigger(intervalTask.getIntervalDuration());
            periodicTrigger.setFixedRate(isFixedRate);
            periodicTrigger.setInitialDelay(intervalTask.getInitialDelayDuration());
            this.trigger = periodicTrigger;
        }

        public Runnable getRunnable() {
            return runnable;
        }

        public Trigger getTrigger() {
            return trigger;
        }
    }
}