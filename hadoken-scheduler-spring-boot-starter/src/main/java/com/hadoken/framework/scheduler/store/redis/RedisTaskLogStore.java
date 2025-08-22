package com.hadoken.framework.scheduler.store.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hadoken.framework.scheduler.config.HadokenSchedulerProperties;
import com.hadoken.framework.scheduler.model.TaskExecutionLog;
import com.hadoken.framework.scheduler.store.TaskLogStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/22 11:20
 */
@Slf4j
public class RedisTaskLogStore implements TaskLogStore {

    private final StringRedisTemplate redisTemplate;
    private final HadokenSchedulerProperties.RedisStoreProperties properties;
    private final ObjectMapper objectMapper;

    public RedisTaskLogStore(StringRedisTemplate redisTemplate, HadokenSchedulerProperties.RedisStoreProperties properties, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }


    @Override
    public void save(String taskId, TaskExecutionLog taskExecutionLog) {
        try {
            String logKey = getLogKey(taskId);
            String logJson = objectMapper.writeValueAsString(taskExecutionLog);
            // LPUSH: 将新日志插入到列表的头部
            redisTemplate.opsForList().leftPush(logKey, logJson);
            // LTRIM: 修剪列表，只保留最新的N条记录
            redisTemplate.opsForList().trim(logKey, 0, properties.getLogRetentionSize() - 1);
        } catch (JsonProcessingException e) {
            log.error("序列化任务执行日志到Redis时失败: {}", taskId, e);
            throw new RuntimeException("任务日志序列化失败", e);
        }
    }

    @Override
    public List<TaskExecutionLog> findRecent(String taskId, int size) {
        String logKey = getLogKey(taskId);
        // LRANGE: 从列表的头部获取最多size条记录
        List<String> logJsons = redisTemplate.opsForList().range(logKey, 0, size - 1);

        if (logJsons == null || logJsons.isEmpty()) {
            return Collections.emptyList();
        }

        return logJsons.stream()
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, TaskExecutionLog.class);
                    } catch (IOException e) {
                        log.error("从Redis反序列化任务日志时失败: {}", taskId, e);
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    private String getLogKey(String taskId) {
        return properties.getLogKeyPrefix() + taskId;
    }
}
