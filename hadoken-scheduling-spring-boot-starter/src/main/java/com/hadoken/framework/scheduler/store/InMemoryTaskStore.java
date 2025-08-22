package com.hadoken.framework.scheduler.store;

import com.hadoken.framework.scheduler.enums.TaskStatus;
import com.hadoken.framework.scheduler.model.TaskDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * TaskStore的默认内存实现。
 * 当没有找到用户自定义的TaskStore Bean时，此实现将被激活。
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/13 11:16
 */
@Slf4j
public class InMemoryTaskStore implements TaskStore {

    private final ConcurrentMap<String, TaskDefinition> taskRegistry = new ConcurrentHashMap<>();

    @Override
    public void save(TaskDefinition definition) {
        log.debug("将任务保存到内存存储中：{}", definition.getId());
        taskRegistry.put(definition.getId(), definition);
    }

    @Override
    public void update(TaskDefinition definition) {
        log.debug("正在内存存储中更新任务：{}", definition.getId());
        taskRegistry.put(definition.getId(), definition);
    }

    @Override
    public void updateStatus(String taskId, TaskStatus status) {
        taskRegistry.computeIfPresent(taskId, (key, definition) -> {
            log.debug("正在内存存储中将任务 '{}' 的状态更新为{} 。", key, status);
            definition.setStatus(status);
            return definition;
        });
    }

    @Override
    public Optional<TaskDefinition> findById(String taskId) {
        return Optional.ofNullable(taskRegistry.get(taskId));
    }

    @Override
    public List<TaskDefinition> findAll() {
        return List.copyOf(taskRegistry.values());
    }

    @Override
    public void deleteById(String taskId) {
        log.debug("从内存存储中删除任务：{}", taskId);
        taskRegistry.remove(taskId);
    }
}