package com.hadoken.framework.scheduling.store;

import com.hadoken.framework.scheduling.enums.TaskStatus;
import com.hadoken.framework.scheduling.model.TaskDefinition;

import java.util.List;
import java.util.Optional;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/22 09:55
 */
public class MybatisTaskStore implements TaskStore {

    private final TaskDefinitionMapper mapper;

    @Override
    public void save(TaskDefinition definition) {
        // MyBatis Mapper会自动处理对象到数据库记录的映射
        // 我们需要一个方法来判断是插入还是更新
        if (mapper.findById(definition.getId()) == null) {
            mapper.insert(definition);
        } else {
            mapper.update(definition);
        }
    }

    @Override
    public void update(TaskDefinition definition) {
        mapper.update(definition);
    }

    @Override
    public void updateStatus(String taskId, TaskStatus status) {
        mapper.updateStatus(taskId, status);
    }

    @Override
    public Optional<TaskDefinition> findById(String taskId) {
        return Optional.ofNullable(mapper.findById(taskId));
    }

    @Override
    public List<TaskDefinition> findAll() {
        return mapper.findAll();
    }

    @Override
    public void deleteById(String taskId) {
        mapper.deleteById(taskId);
    }
}