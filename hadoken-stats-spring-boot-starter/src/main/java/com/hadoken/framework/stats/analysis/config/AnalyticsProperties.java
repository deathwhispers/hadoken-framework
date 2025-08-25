package com.hadoken.framework.stats.analysis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 用于绑定 application.yml 中 "analytics.starter" 前缀的配置。
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/24 14:01
 */
@ConfigurationProperties(prefix = "analytics.starter")
@Data
public class AnalyticsProperties {
    /**
     * 是否启用本Starter的全部功能。默认为true。
     */
    private boolean enabled = true;
    /**
     * API相关配置
     */
    private Api api = new Api();

    @Data
    public static class Api {
        /**
         * 是否启用由Starter提供的通用查询API。默认为true。
         */
        private boolean enabled = true;
    }
}