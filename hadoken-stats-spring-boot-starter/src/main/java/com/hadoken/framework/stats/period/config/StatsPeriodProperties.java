package com.hadoken.framework.stats.period.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/11 10:48
 */
@Data
@ConfigurationProperties(prefix = "rhy.stats.period")
public class StatsPeriodProperties {

    /**
     * 默认时区，如 Asia/Shanghai
     */
    private String zoneId = "Asia/Shanghai";

    /**
     * 是否启用调试日志
     */
    private Set<String> enabledTypes = new HashSet<>();
}
