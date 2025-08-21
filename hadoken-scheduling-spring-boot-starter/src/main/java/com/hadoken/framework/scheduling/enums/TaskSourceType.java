package com.hadoken.framework.scheduling.enums;

/**
 * 任务的来源类型。
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/13 11:13
 */
public enum TaskSourceType {
    /**
     * 来源于代码中的注解。
     */
    ANNOTATED,
    /**
     * 来源于数据库（或UI）的动态创建。
     */
    DYNAMIC
}