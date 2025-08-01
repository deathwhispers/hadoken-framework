package com.hadoken.framework.mq.core.message;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Redis Stream Message 抽象类
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/03/02 11:52
 */
public abstract class AbstractStreamMessage extends AbstractRedisMessage {

    /**
     * 获得 Redis Stream Key
     *
     * @return Channel
     */
    @JsonIgnore
    public abstract String getStreamKey();
}
