package com.hadoken.framework.scheduling.enums;

/**
 * 任务的持久化状态
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/13 11:13
 */
public enum TaskStatus {
    /**
     * 任务已停止，不会被调度执行。
     */
    STOPPED,
    /**
     * 任务处于运行中（已提交给调度器），等待下一次触发。
     */
    RUNNING
}