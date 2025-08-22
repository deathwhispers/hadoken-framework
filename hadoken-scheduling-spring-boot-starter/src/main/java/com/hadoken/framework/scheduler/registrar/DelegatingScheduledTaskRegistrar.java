//package com.hadoken.framework.scheduling.registrar;
//
//import com.hadoken.framework.scheduling.annotation.ManagedScheduled;
//import com.hadoken.framework.scheduling.enums.TaskSourceType;
//import com.hadoken.framework.scheduling.enums.TaskStatus;
//import com.hadoken.framework.scheduling.enums.TriggerType;
//import com.hadoken.framework.scheduling.manager.TaskManager;
//import com.hadoken.framework.scheduling.model.TaskDefinition;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.aop.support.AopUtils;
//import org.springframework.beans.factory.NoSuchBeanDefinitionException;
//import org.springframework.context.ApplicationContext;
//import org.springframework.core.annotation.AnnotationUtils;
//import org.springframework.scheduling.Trigger;
//import org.springframework.scheduling.config.ScheduledTaskRegistrar;
//import org.springframework.scheduling.support.CronTrigger;
//import org.springframework.scheduling.support.PeriodicTrigger;
//import org.springframework.scheduling.support.ScheduledMethodRunnable;
//
//import java.lang.reflect.Method;
//import java.time.Duration;
//
///**
// * 代理模式的ScheduledTaskRegistrar，是我们实现无感增强的核心挂钩。
// * 它会拦截所有通过 {@link org.springframework.scheduling.annotation.Scheduled}注解注册的任务，并将其信息同步到 TaskManager 和 TaskStore
// *
// * @author yanggj
// * @version 1.0.0
// * Created on 2025/8/13 11:19
// */
//@Slf4j
//public class DelegatingScheduledTaskRegistrar extends ScheduledTaskRegistrar {
//
//    private final TaskManager taskManager;
//    private final ApplicationContext applicationContext;
//
//    public DelegatingScheduledTaskRegistrar(TaskManager taskManager, ApplicationContext applicationContext) {
//        this.taskManager = taskManager;
//        this.applicationContext = applicationContext;
//    }
//
//    @Override
//    public void addTriggerTask(Runnable task, Trigger trigger) {
//        // 这是所有@Scheduled任务的最终入口，我们在这里进行拦截和处理
//        TaskDefinition definition = buildTaskDefinition(task, trigger);
//
//        // 将任务定义同步到TaskManager和TaskStore，并获取被包装后的Runnable
//        Runnable wrappedTask = taskManager.registerAndSync(definition, task);
//
//        // 最后，将包装后的任务和原始触发器交给父类去完成真正的调度注册
//        super.addTriggerTask(wrappedTask, trigger);
//    }
//
//    /**
//     * 从Runnable和Trigger中解析出TaskDefinition的元数据。
//     */
//    private TaskDefinition buildTaskDefinition(Runnable runnable, Trigger trigger) {
//        TaskDefinition.TaskDefinitionBuilder builder = TaskDefinition.builder();
//
//        // 尝试从Runnable中解析出方法和Bean信息
//        // Spring创建的调度任务Runnable通常是ScheduledMethodRunnable类型
//        if (runnable instanceof ScheduledMethodRunnable scheduledMethodRunnable) {
//            Method method = scheduledMethodRunnable.getMethod();
//            Object targetBean = scheduledMethodRunnable.getTarget();
//            Class<?> targetClass = AopUtils.getTargetClass(targetBean);
//            String beanName = findBeanName(targetClass);
//
//            builder.beanName(beanName).methodName(method.getName());
//
//            // 检查是否存在我们补充的@ScheduledTask注解
//            ManagedScheduled taskDescriptorAnn = AnnotationUtils.findAnnotation(method, ManagedScheduled.class);
//            if (taskDescriptorAnn != null) {
//                builder.id(taskDescriptorAnn.id()).description(taskDescriptorAnn.description());
//            } else {
//                // 如果没有，生成一个默认ID
//                builder.id(beanName + "#" + method.getName());
//                builder.description("Annotated task without @TaskDescriptor");
//            }
//        } else {
//            // 对于非Spring方法调度的任务（如Lambda表达式），我们无法获取方法信息
//            builder.id("dynamic-task-" + runnable.hashCode());
//            builder.description("A dynamically submitted task");
//        }
//
//        // 解析Trigger类型和值
//        if (trigger instanceof CronTrigger cronTrigger) {
//            builder.triggerType(TriggerType.CRON).triggerValue(cronTrigger.getExpression());
//        } else if (trigger instanceof PeriodicTrigger periodicTrigger) {
//            Duration period = periodicTrigger.getPeriodDuration();
//            if (periodicTrigger.isFixedRate()) {
//                builder.triggerType(TriggerType.FIXED_RATE);
//            } else {
//                builder.triggerType(TriggerType.FIXED_DELAY);
//            }
//            builder.triggerValue(period.toMillis() + "ms");
//        } else {
//            // 对于其他未知类型的Trigger，我们无法持久化其定义
//            log.warn("Unknown trigger type [{}], its definition cannot be persisted.", trigger.getClass().getSimpleName());
//        }
//
//        // 默认状态和来源
//        builder.status(TaskStatus.STOPPED) // 初始状态为停止，由TaskManager决定是否启动
//                .sourceType(TaskSourceType.ANNOTATED);
//
//        return builder.build();
//    }
//
//    private String findBeanName(Class<?> beanClass) {
//        try {
//            // Spring的ApplicationContext可以根据类型找到其注册的beanName
//            String[] beanNames = applicationContext.getBeanNamesForType(beanClass);
//            if (beanNames.length > 0) {
//                // 通常只有一个，我们取第一个
//                return beanNames[0];
//            }
//        } catch (NoSuchBeanDefinitionException e) {
//            // 忽略
//        }
//        return beanClass.getName();
//    }
//
//
//}