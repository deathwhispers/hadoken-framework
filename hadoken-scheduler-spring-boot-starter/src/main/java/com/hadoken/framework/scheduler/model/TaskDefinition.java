package com.hadoken.framework.scheduler.model;

import com.hadoken.framework.scheduler.enums.TaskSourceType;
import com.hadoken.framework.scheduler.enums.TaskStatus;
import com.hadoken.framework.scheduler.enums.TriggerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务定义的规范化POJO (Plain Old Java Object)。
 * 这是框架内部用来表示一个任务完整定义的核心数据结构，它与任何持久化技术无关。
 * TaskStore接口将使用此对象进行数据交换。
 * import lombok.NoArgsConstructor;
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/13 11:14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDefinition {

    /**
     * 任务唯一ID。
     */
    private String id;

    /**
     * 任务描述。
     */
    private String description;

    /**
     * 任务来源类型 (代码注解 / 数据库动态创建)。
     */
    private TaskSourceType sourceType;

    /**
     * 执行任务的Bean的名称。
     */
    private String beanName;

    /**
     * 执行任务的方法名。
     */
    private String methodName;

    /**
     * 触发器类型 (CRON, FIXED_RATE, etc.)。
     */
    private TriggerType triggerType;

    /**
     * 触发器具体的值。
     * - 对于CRON, 这是cron表达式。
     * - 对于FIXED_RATE/FIXED_DELAY, 这是时间间隔字符串 (e.g., "5s", "1m")。
     */
    private String triggerValue;

    /**
     * 任务的持久化状态 (STOPPED / RUNNING)。
     * 应用重启后将根据此状态决定是否自动运行。
     */
    private TaskStatus status;

    /**
     * 分布式锁的最长持有时间字符串。
     */
    private String lockAtMostForString;
}