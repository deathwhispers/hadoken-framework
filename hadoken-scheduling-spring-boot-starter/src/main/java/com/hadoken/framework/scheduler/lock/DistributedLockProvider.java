package com.hadoken.framework.scheduler.lock;

import java.time.Duration;
import java.util.Optional;

/**
 * 分布式锁提供者接口。
 * 框架的核心逻辑将依赖此接口来获取和释放锁。
 * 用户可以通过在Spring容器中注册一个此接口的实现Bean来激活分布式锁功能。
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/14 20:26
 */
public interface DistributedLockProvider {

    /**
     * 代表一个已获取的锁。
     * 实现AutoCloseable接口，以便于使用try-with-resources语法自动释放锁。
     */
    interface Lock extends AutoCloseable {
        @Override
        void close(); // close()方法应实现释放锁的逻辑
    }

    /**
     * 尝试获取一个锁。
     *
     * @param lockKey       锁的唯一标识 (通常是任务ID)
     * @param lockAtMostFor 锁的最长持有时间。这是一个安全保障，防止因节点崩溃导致死锁。锁会在这个时间后自动过期。
     * @return 如果成功获取锁，则返回一个包含Lock实例的Optional；否则返回空的Optional。
     */
    Optional<Lock> tryLock(String lockKey, Duration lockAtMostFor);

}
