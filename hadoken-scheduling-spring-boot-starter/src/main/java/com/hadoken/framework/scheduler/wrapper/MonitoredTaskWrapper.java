package com.hadoken.framework.scheduler.wrapper;

import com.hadoken.framework.scheduler.lock.DistributedLockProvider;
import com.hadoken.framework.scheduler.model.ManagedTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 受监控的任务包装器，是实际被调度器执行的Runnable。
 * 它像一个代理，包装了用户的业务逻辑，并在此基础上实现了：
 * 1. 【并发防护】: 防止同一个任务因执行时间过长而与下一次调度重叠执行。
 * 2. 【生命周期日志】: 自动记录任务开始、成功、失败的日志。
 * 3. 【性能统计】: 精确计时，并在任务执行后更新ManagedTask中的统计数据。
 * 4. 【异常隔离】: 捕获所有运行时异常，防止因业务代码错误导致调度线程中断。
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/13 11:33
 */
@Slf4j
public class MonitoredTaskWrapper implements Runnable {

    private final ManagedTask managedTask;
    // 注入锁提供者
    private final Optional<DistributedLockProvider> lockProviderOpt;
    /**
     * 使用原子布尔值作为简单的执行锁。
     * 当任务开始时，将其设置为true；任务结束时，设置为false。
     * 如果任务开始时发现其值已为true，则说明上一次执行尚未完成，本次将跳过。
     * 这对于fixedRate类型的任务尤其重要。
     */
    private final AtomicBoolean running = new AtomicBoolean(false);

    public MonitoredTaskWrapper(ManagedTask managedTask, Optional<DistributedLockProvider> lockProviderOpt) {
        this.managedTask = managedTask;
        this.lockProviderOpt = lockProviderOpt;
    }

    @Override
    public void run() {

        // [优化] 检查是否需要执行分布式锁逻辑
        String lockConfig = managedTask.getDefinition().getLockAtMostForString();
        boolean needsLocking = lockProviderOpt.isPresent() && StringUtils.hasText(lockConfig);

        if (needsLocking) {
            // 如果配置了分布式锁，则执行锁逻辑
            executeWithLock(lockConfig);
        } else {
            // 未配置分布式锁，执行原始逻辑
            executeTaskLogic();
        }
    }

    private void executeWithLock(String lockConfig) {
        DistributedLockProvider lockProvider = lockProviderOpt.get();
        String taskId = managedTask.getDefinition().getId();
        try {
            Duration lockAtMostFor = DurationStyle.detectAndParse(lockConfig);
            Optional<DistributedLockProvider.Lock> lockOpt = lockProvider.tryLock(taskId, lockAtMostFor);
            if (lockOpt.isPresent()) {
                // 成功获取锁，使用try-with-resources确保锁被释放
                try (DistributedLockProvider.Lock lock = lockOpt.get()) {
                    log.debug("已获取任务 '{}' 的锁。正在执行...", taskId);
                    executeTaskLogic();
                }
            } else {
                // 未获取到锁，说明其他节点正在执行
                log.debug("无法获取任务 '{}' 的锁。跳过执行。", taskId);
            }
        } catch (Exception e) {
            log.error("任务 '{}' 在分布式锁操作期间出错。", taskId, e);
        }
    }

    /**
     * 将原始的任务执行、监控、统计逻辑封装到一个方法中
     */
    private void executeTaskLogic() {
        // 尝试获取执行权，如果当前已经在运行，则直接返回，并打印警告日志。
        if (!running.compareAndSet(false, true)) {
            log.warn("任务 '{}' 仍在运行。为防止重叠，跳过此次执行。",
                    managedTask.getDefinition().getId());
            return;
        }

        LocalDateTime startTime = null;
        try {
            startTime = LocalDateTime.now();
            // 在执行前，更新ManagedTask中的“上次执行时间”
            managedTask.setLastExecutionTime(startTime);

            log.info("任务 '{}' 开始执行...", managedTask.getDefinition().getId());

            // 核心：执行用户定义的原始业务逻辑
            managedTask.getOriginalRunnable().run();

            // 任务成功执行
            Duration duration = Duration.between(startTime, Instant.now());
            log.info("任务 '{}' 在 {}ms 内成功完成执行。",
                    managedTask.getDefinition().getId(), duration.toMillis());

            // 更新统计信息
            managedTask.updateOnSuccess(duration);

        } catch (Throwable t) {
            // 任务执行失败
            log.error("任务 '{}' 执行失败.", managedTask.getDefinition().getId(), t);

            if (startTime != null) {
                Duration duration = Duration.between(startTime, Instant.now());
                // 更新统计信息
                managedTask.updateOnFailure(duration, t);
            }

        } finally {
            // 无论成功还是失败，最终都必须释放锁，以便下一次可以正常调度。
            running.set(false);
        }
    }

}