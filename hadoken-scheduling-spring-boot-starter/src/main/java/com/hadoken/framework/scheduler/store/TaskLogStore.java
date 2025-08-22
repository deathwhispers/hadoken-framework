package com.hadoken.framework.scheduler.store;

import com.hadoken.framework.scheduler.model.TaskExecutionLog;

import java.util.List;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/22 10:15
 */
public interface TaskLogStore {

    void save(String taskId, TaskExecutionLog log);

    List<TaskExecutionLog> findRecent(String taskId, int size);

}
