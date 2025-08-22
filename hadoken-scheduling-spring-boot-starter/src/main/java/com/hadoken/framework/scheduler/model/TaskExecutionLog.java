package com.hadoken.framework.scheduler.model;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 任务单次执行的日志记录。
 *
 * @param startTime    任务开始执行的时间点
 * @param duration     任务执行耗时
 * @param success      任务是否成功执行
 * @param errorMessage 如果执行失败，记录错误信息
 * @param instanceId   执行此次任务的应用实例ID (用于集群环境)
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/13 11:15
 */
public record TaskExecutionLog(
        LocalDateTime startTime,
        Duration duration,
        boolean success,
        String errorMessage,
        String instanceId // 新增字段，用于集群追踪
) {
}