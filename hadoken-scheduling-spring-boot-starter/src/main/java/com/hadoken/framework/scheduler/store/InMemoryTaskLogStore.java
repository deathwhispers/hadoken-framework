package com.hadoken.framework.scheduler.store;

import com.hadoken.framework.scheduler.model.TaskExecutionLog;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/22 10:16
 */
public class InMemoryTaskLogStore implements TaskLogStore {

    private final ConcurrentMap<String, Queue<TaskExecutionLog>> logRegistry = new ConcurrentHashMap<>();

    @Override
    public void save(String taskId, TaskExecutionLog log) {
        Queue<TaskExecutionLog> logs = logRegistry.computeIfAbsent(taskId, k -> new ConcurrentLinkedQueue<>());
        if (logs.size() >= 100) { // Can be configured
            logs.poll();
        }
        logs.offer(log);
    }

    @Override
    public List<TaskExecutionLog> findRecent(String taskId, int size) {
        Queue<TaskExecutionLog> logs = logRegistry.get(taskId);
        if (logs == null) {
            return Collections.emptyList();
        }
        List<TaskExecutionLog> logList = new ArrayList<>(logs);
        logList.sort(Comparator.comparing(TaskExecutionLog::startTime).reversed());
        return logList.stream().limit(size).toList();
    }
}

