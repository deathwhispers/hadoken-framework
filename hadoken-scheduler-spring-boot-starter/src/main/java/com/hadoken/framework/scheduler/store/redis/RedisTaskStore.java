package com.hadoken.framework.scheduler.store.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hadoken.framework.scheduler.config.HadokenSchedulerProperties;
import com.hadoken.framework.scheduler.enums.TaskStatus;
import com.hadoken.framework.scheduler.model.TaskDefinition;
import com.hadoken.framework.scheduler.store.TaskStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/22 11:20
 */
@Slf4j
public class RedisTaskStore implements TaskStore {

    private final StringRedisTemplate redisTemplate;
    private final HadokenSchedulerProperties.RedisStoreProperties properties;
    private final ObjectMapper objectMapper;

    public RedisTaskStore(StringRedisTemplate redisTemplate, HadokenSchedulerProperties.RedisStoreProperties properties, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }


    @Override
    public void save(TaskDefinition definition) {
        try {
            String json = objectMapper.writeValueAsString(definition);
            // 存储任务定义详情
            redisTemplate.opsForValue().set(getDefinitionKey(definition.getId()), json);
            // 将任务ID添加到总集合中
            redisTemplate.opsForSet().add(properties.getAllTasksKey(), definition.getId());
        } catch (JsonProcessingException e) {
            log.error("序列化任务定义到Redis时失败: {}", definition.getId(), e);
            throw new RuntimeException("任务序列化失败", e);
        }
    }

    @Override
    public void update(TaskDefinition definition) {
        // 对于Redis的set操作来说，它本身就是覆盖/更新
        save(definition);
    }

    @Override
    public void updateStatus(String taskId, TaskStatus status) {
        findById(taskId).ifPresent(definition -> {
            definition.setStatus(status);
            save(definition);
        });
    }

    @Override
    public Optional<TaskDefinition> findById(String taskId) {
        String json = redisTemplate.opsForValue().get(getDefinitionKey(taskId));
        if (json == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(json, TaskDefinition.class));
        } catch (IOException e) {
            log.error("从Redis反序列化任务定义时失败: {}", taskId, e);
            return Optional.empty();
        }
    }

    @Override
    public List<TaskDefinition> findAll() {
        Set<String> taskIds = redisTemplate.opsForSet().members(properties.getAllTasksKey());
        if (CollectionUtils.isEmpty(taskIds)) {
            return Collections.emptyList();
        }
        List<String> definitionKeys = taskIds.stream().map(this::getDefinitionKey).toList();
        List<String> results = redisTemplate.opsForValue().multiGet(definitionKeys);
        if (results == null) {
            return Collections.emptyList();
        }
        return results.stream()
                .filter(java.util.Objects::nonNull)
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, TaskDefinition.class);
                    } catch (IOException e) {
                        log.error("在findAll期间反序列化某个任务时失败", e);
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String taskId) {
        redisTemplate.delete(getDefinitionKey(taskId));
        redisTemplate.opsForSet().remove(properties.getAllTasksKey(), taskId);
    }

    private String getDefinitionKey(String taskId) {
        return properties.getDefinitionKeyPrefix() + taskId;
    }
}
