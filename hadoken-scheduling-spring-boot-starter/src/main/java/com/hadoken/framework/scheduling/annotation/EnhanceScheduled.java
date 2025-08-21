package com.hadoken.framework.scheduling.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 增强的、可管理的调度任务注解。
 * 这是 @Scheduled 的超集，为用户提供一站式配置。
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/14 23:05
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scheduled // 确保Spring原生调度能识别它
public @interface EnhanceScheduled {

    /**
     * 任务的唯一ID。**必须提供**。
     * 用于运行时控制和持久化。
     */
    String id();

    /**
     * 任务的描述信息。
     */
    String description() default "";

    /**
     * @see Scheduled#cron()
     */
    @AliasFor(annotation = Scheduled.class, attribute = "cron")
    String cron() default "";

    /**
     * @see Scheduled#zone()
     */
    @AliasFor(annotation = Scheduled.class, attribute = "zone")
    String zone() default "";

    /**
     * @see Scheduled#fixedRate()
     */
    @AliasFor(annotation = Scheduled.class, attribute = "fixedRate")
    long fixedRate() default -1L;

    /**
     * @see Scheduled#fixedRateString()
     */
    @AliasFor(annotation = Scheduled.class, attribute = "fixedRateString")
    String fixedRateString() default "";

    /**
     * @see Scheduled#fixedDelay()
     */
    @AliasFor(annotation = Scheduled.class, attribute = "fixedDelay")
    long fixedDelay() default -1L;

    /**
     * @see Scheduled#fixedDelayString()
     */
    @AliasFor(annotation = Scheduled.class, attribute = "fixedDelayString")
    String fixedDelayString() default "";

    /**
     * @see Scheduled#initialDelay()
     */
    @AliasFor(annotation = Scheduled.class, attribute = "initialDelay")
    long initialDelay() default -1L;

    /**
     * @see Scheduled#initialDelayString()
     */
    @AliasFor(annotation = Scheduled.class, attribute = "initialDelayString")
    String initialDelayString() default "";

    @AliasFor(annotation = Scheduled.class, attribute = "timeUnit")
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    @AliasFor(annotation = Scheduled.class, attribute = "scheduler")
    String scheduler() default "";

    /**
     * 分布式锁的最长持有时间。格式遵循java.time.Duration的字符串表示法，如 "PT1M" (1分钟), "PT30S" (30秒)。
     * 这是一个强制性的安全设置，防止节点宕机后锁无法释放。
     * 如果设置了此项，则该任务会尝试获取分布式锁。
     */
    String lockAtMostForString() default "";
}