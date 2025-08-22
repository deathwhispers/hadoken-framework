package com.hadoken.framework.scheduler.store;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hadoken.framework.scheduler.model.TaskExecutionLog;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * MyBatis-Plus的实体类，用于映射任务执行日志表。
 * 表名可通过 application.yml 中的 "hadoken.scheduler.store.jdbc.log-table-name"进行自定义。
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/22 10:58
 */
@Data
@TableName("${hadoken.scheduler.store.jdbc.log-table-name:t_schedule_task_log}")
public class TaskLogEntity {

    @TableId
    private String logId;

    private String taskId;

    private LocalDateTime startTime;

    // 将Duration转换为long存储
    private long durationMillis;

    private boolean success;

    private String errorMessage;

    private String instanceId;

    public static TaskLogEntity from(String taskId, TaskExecutionLog log) {
        if (log == null) return null;
        TaskLogEntity entity = new TaskLogEntity();
        entity.setLogId(java.util.UUID.randomUUID().toString());
        entity.setTaskId(taskId);
        entity.setStartTime(log.startTime());
        entity.setDurationMillis(log.duration().toMillis());
        entity.setSuccess(log.success());
        entity.setErrorMessage(log.errorMessage());
        entity.setInstanceId(log.instanceId());
        return entity;
    }

    public TaskExecutionLog toTaskExecutionLog() {
        return new TaskExecutionLog(
                this.startTime,
                Duration.ofMillis(this.durationMillis),
                this.success,
                this.errorMessage,
                this.instanceId
        );
    }
}