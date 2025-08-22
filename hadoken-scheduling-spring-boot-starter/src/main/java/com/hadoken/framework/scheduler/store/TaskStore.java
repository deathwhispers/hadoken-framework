package com.hadoken.framework.scheduler.store;


import com.hadoken.framework.scheduler.enums.TaskStatus;
import com.hadoken.framework.scheduler.model.TaskDefinition;

import java.util.List;
import java.util.Optional;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/13 11:15
 * 任务存储服务的核心抽象接口。
 * 定义了任务定义的持久化CRUD操作。
 */
public interface TaskStore {

    /**
     * 保存一个新的任务定义。
     */
    void save(TaskDefinition definition);

    /**
     * 更新一个已有的任务定义。
     */
    void update(TaskDefinition definition);

    /**
     * 更新指定任务的状态。
     */
    void updateStatus(String taskId, TaskStatus status);

    /**
     * 根据ID查找一个任务定义。
     */
    Optional<TaskDefinition> findById(String taskId);

    /**
     * 查找所有任务定义。
     */
    List<TaskDefinition> findAll();

    /**
     * 根据ID删除一个任务定义。
     */
    void deleteById(String taskId);

}