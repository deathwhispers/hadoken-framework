package com.hadoken.framework.scheduler.store.mybatis;

import com.hadoken.framework.scheduler.enums.TaskStatus;
import com.hadoken.framework.scheduler.model.TaskDefinition;
import com.hadoken.framework.scheduler.store.TaskStore;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/22 09:55
 */
public class MybatisTaskStore implements TaskStore {

    private final TaskDefinitionMapper mapper;

    public MybatisTaskStore(TaskDefinitionMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void save(TaskDefinition definition) {
        TaskDefinitionEntity entity = TaskDefinitionEntity.from(definition);
        if (mapper.selectById(entity.getId()) != null) {
            mapper.updateById(entity);
        } else {
            mapper.insert(entity);
        }
    }

    @Override
    public void update(TaskDefinition definition) {
        mapper.updateById(TaskDefinitionEntity.from(definition));
    }

    @Override
    public void updateStatus(String taskId, TaskStatus status) {
        TaskDefinitionEntity entity = new TaskDefinitionEntity();
        entity.setId(taskId);
        entity.setStatus(status);
        mapper.updateById(entity);
    }

    @Override
    public Optional<TaskDefinition> findById(String taskId) {
        return Optional.ofNullable(mapper.selectById(taskId))
                .map(TaskDefinitionEntity::toTaskDefinition);
    }

    @Override
    public List<TaskDefinition> findAll() {
        return mapper.selectList(null).stream()
                .map(TaskDefinitionEntity::toTaskDefinition)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String taskId) {
        mapper.deleteById(taskId);
    }
}