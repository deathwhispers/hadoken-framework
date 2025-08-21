package com.hadoken.framework.scheduling.model;

import com.hadoken.framework.scheduling.enums.TaskStatus;
import com.hadoken.framework.scheduling.lock.DistributedLockProvider;
import com.hadoken.framework.scheduling.wrapper.MonitoredTaskWrapper;
import lombok.Getter;
import lombok.ToString;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 被管理任务的运行时（In-Memory）模型。
 * 它是有状态的，包含了任务的静态定义以及所有动态的运行时信息，
 * 例如调度句柄(Future)、执行统计和最近的日志。
 * TaskManager的核心就是管理这些ManagedTask实例。
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/13 11:30
 */
@Getter
@ToString(exclude = {"scheduledFuture", "runnable", "executionLogs"})
public class ManagedTask {

    /**
     * 任务的静态定义（通常来自TaskStore）。
     */
    private final TaskDefinition definition;

    /**
     * 用户定义的原始业务逻辑。
     */
    private final Runnable originalRunnable;

    /**
     * 最终被调度的、包装了监控逻辑的Runnable。
     */
    private final Runnable runnable;

    // --- 运行时易变状态 ---

    /**
     * Spring调度器返回的Future，用于取消任务。
     */
    private volatile ScheduledFuture<?> scheduledFuture;

    // --- 统计信息 (需线程安全) ---
    private final AtomicLong successCount = new AtomicLong(0);
    private final AtomicLong failureCount = new AtomicLong(0);
    private final AtomicLong totalExecutionTimeMillis = new AtomicLong(0);
    private volatile Instant lastExecutionTime;
    private volatile boolean lastExecutionSuccess;
    private volatile String lastErrorMessage;

    /**
     * 存储最近的执行日志（内存中）。
     */
    private final Queue<TaskExecutionLog> executionLogs;
    private final int logRetentionSize;

    // (可选) 当前执行任务的应用实例ID
    private final String instanceId;

    public ManagedTask(TaskDefinition definition, Runnable originalRunnable, String instanceId,
                       int logRetentionSize,
                       Optional<DistributedLockProvider> lockProviderOpt
    ) {
        this.definition = definition;
        this.originalRunnable = originalRunnable;
        this.logRetentionSize = logRetentionSize > 0 ? logRetentionSize : 100;
        this.executionLogs = new ConcurrentLinkedQueue<>();
        this.instanceId = instanceId;

        // 创建监控包装器
        this.runnable = new MonitoredTaskWrapper(this, lockProviderOpt);
    }

    // --- 线程安全的状态更新方法 ---
    public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        // 在停止任务时，旧的future会被置为null，所以这里需要同步块来保证可见性和原子性
        synchronized (this) {
            this.scheduledFuture = scheduledFuture;
        }
    }

    public void updateOnSuccess(Duration duration) {
        this.successCount.incrementAndGet();
        this.totalExecutionTimeMillis.addAndGet(duration.toMillis());
        this.lastExecutionSuccess = true;
        this.lastErrorMessage = null;
        addLog(new TaskExecutionLog(this.lastExecutionTime, duration, true, null, this.instanceId));
    }

    public void updateOnFailure(Duration duration, Throwable throwable) {
        this.failureCount.incrementAndGet();
        this.totalExecutionTimeMillis.addAndGet(duration.toMillis());
        this.lastExecutionSuccess = false;
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }
        this.lastErrorMessage = rootCause.getClass().getSimpleName() + ": " + rootCause.getMessage();
        addLog(new TaskExecutionLog(this.lastExecutionTime, duration, false, this.lastErrorMessage, this.instanceId));
    }

    public void setLastExecutionTime(Instant startTime) {
        this.lastExecutionTime = startTime;
    }

    private void addLog(TaskExecutionLog logEntry) {
        if (this.executionLogs.size() >= this.logRetentionSize) {
            this.executionLogs.poll();
        }
        this.executionLogs.offer(logEntry);
    }

    public TaskStatus getStatus() {
        if (this.scheduledFuture == null || this.scheduledFuture.isCancelled()) {
            return TaskStatus.STOPPED;
        }
        return TaskStatus.RUNNING;
    }

    public Instant getNextExecutionTime() {
        ScheduledFuture<?> future = this.scheduledFuture;
        if (future != null && !future.isDone()) {
            long delayMillis = future.getDelay(TimeUnit.MILLISECONDS);
            if (delayMillis > 0) {
                return Instant.now().plusMillis(delayMillis);
            }
        }
        return null;
    }

    public long getAverageExecutionTimeMillis() {
        long totalCount = successCount.get() + failureCount.get();
        if (totalCount == 0) {
            return 0;
        }
        return totalExecutionTimeMillis.get() / totalCount;
    }
}