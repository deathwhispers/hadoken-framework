package com.hadoken.framework.scheduler.manager;


import com.hadoken.framework.scheduler.model.ManagedTask;
import com.hadoken.framework.scheduler.model.TaskDefinition;
import org.springframework.scheduling.Trigger;

import java.util.Collection;
import java.util.Optional;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/13 11:19
 */
public interface TaskManager {

    /**
     * 在应用启动时调用，从TaskStore加载并初始化所有任务。
     */
    void initializeTasks();

    /**
     * 注册一个通过注解发现的任务，并将其定义与持久化存储同步。
     *
     * @param definition       从注解解析出的任务定义
     * @param originalRunnable 原始的、未被包装的Runnable
     * @return 被MonitoredTaskWrapper包装后的Runnable
     */
    Runnable registerAndSync(TaskDefinition definition, Runnable originalRunnable);

    /**
     * 由Configurer调用，用于处理所有通过注解发现的任务。
     * 该方法负责同步定义、创建运行时实例并根据状态进行调度。
     *
     * @param definition       从注解解析出的任务定义
     * @param originalRunnable 原始的、未被包装的Runnable
     * @param trigger          Spring解析出的触发器
     */
    void processAnnotatedTask(TaskDefinition definition, Runnable originalRunnable, Trigger trigger);

    /**
     * 启动一个任务。
     */
    void start(String taskId);

    /**
     * 停止一个任务。
     */
    void stop(String taskId);

    /**
     * 触发一个任务立即执行一次。
     */
    void triggerOnce(String taskId);

    /**
     * 获取一个任务的运行时信息。
     */
    Optional<ManagedTask> getTask(String taskId);

    /**
     * 获取所有任务的运行时信息。
     */
    Collection<ManagedTask> getAllTasks();

    /**
     * 动态创建一个新任务，并将其持久化。
     * 这个任务的来源将被标记为 DYNAMIC。
     *
     * @param definition 任务定义
     * @throws IllegalStateException 如果任务ID已存在或无法解析Bean/Method
     */
    void createDynamicTask(TaskDefinition definition);

    /**
     * 彻底删除一个任务，包括停止其调度、从运行时缓存中移除以及从持久化存储中删除。
     *
     * @param taskId 任务ID
     */
    void deleteTask(String taskId);

}

