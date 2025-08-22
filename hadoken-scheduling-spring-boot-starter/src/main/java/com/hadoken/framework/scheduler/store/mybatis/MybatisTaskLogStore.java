package com.hadoken.framework.scheduler.store.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hadoken.framework.scheduler.model.TaskExecutionLog;
import com.hadoken.framework.scheduler.store.TaskLogStore;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/22 11:02
 */
public class MybatisTaskLogStore implements TaskLogStore {

    private final TaskLogMapper mapper;

    public MybatisTaskLogStore(TaskLogMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void save(String taskId, TaskExecutionLog log) {
        mapper.insert(TaskLogEntity.from(taskId, log));
    }

    @Override
    public List<TaskExecutionLog> findRecent(String taskId, int size) {
        QueryWrapper<TaskLogEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("task_id", taskId)
                .orderByDesc("start_time")
                .last("LIMIT " + size);

        return mapper.selectList(wrapper).stream()
                .map(TaskLogEntity::toTaskExecutionLog)
                .collect(Collectors.toList());
    }
}

