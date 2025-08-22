package com.hadoken.framework.scheduler.store.mybatis;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hadoken.framework.scheduler.enums.TaskSourceType;
import com.hadoken.framework.scheduler.enums.TaskStatus;
import com.hadoken.framework.scheduler.enums.TriggerType;
import com.hadoken.framework.scheduler.model.TaskDefinition;
import lombok.Data;

/**
 * MyBatis-Plus的实体类，用于映射任务定义表。
 * 表名可通过 application.yml 进行自定义。
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/22 10:54
 */
@Data
@TableName("${hadoken.scheduler.store.mybatis.definition-table-name:t_schedule_task_definition}")
public class TaskDefinitionEntity {

    @TableId
    private String id;

    private String description;

    private TaskSourceType sourceType;

    private String beanName;

    private String methodName;

    private TriggerType triggerType;

    private String triggerValue;

    private TaskStatus status;

    private String lockAtMostForString;

    /**
     * 从框架内部模型转换为持久化实体
     */
    public static TaskDefinitionEntity from(TaskDefinition definition) {
        if (definition == null) return null;
        TaskDefinitionEntity entity = new TaskDefinitionEntity();
        entity.setId(definition.getId());
        entity.setDescription(definition.getDescription());
        entity.setSourceType(definition.getSourceType());
        entity.setBeanName(definition.getBeanName());
        entity.setMethodName(definition.getMethodName());
        entity.setTriggerType(definition.getTriggerType());
        entity.setTriggerValue(definition.getTriggerValue());
        entity.setStatus(definition.getStatus());
        entity.setLockAtMostForString(definition.getLockAtMostForString());
        return entity;
    }

    /**
     * 从持久化实体转换为框架内部模型
     */
    public TaskDefinition toTaskDefinition() {
        return TaskDefinition.builder()
                .id(this.id)
                .description(this.description)
                .sourceType(this.sourceType)
                .beanName(this.beanName)
                .methodName(this.methodName)
                .triggerType(this.triggerType)
                .triggerValue(this.triggerValue)
                .status(this.status)
                .lockAtMostForString(this.lockAtMostForString)
                .build();
    }
}