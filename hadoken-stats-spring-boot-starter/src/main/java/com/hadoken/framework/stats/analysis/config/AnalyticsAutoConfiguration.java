package com.hadoken.framework.stats.analysis.config;

import com.hadoken.framework.stats.analysis.core.builder.DefaultQueryBuilder;
import com.hadoken.framework.stats.analysis.core.builder.QueryBuilder;
import com.hadoken.framework.stats.analysis.core.mapper.GenericMetricMapper;
import com.hadoken.framework.stats.analysis.core.service.GenericStatisticsService;
import com.hadoken.framework.stats.analysis.core.transform.ChartDataTransformer;
import com.hadoken.framework.stats.analysis.web.GenericStatsController;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 统计分析模块自动化配置
 *
 * @author Gemini
 * @author yanggj
 * @version 2.0.0
 */
@Configuration
@EnableConfigurationProperties(AnalyticsProperties.class)
@ConditionalOnProperty(prefix = "analytics.starter", name = "enabled", havingValue = "true", matchIfMissing = true)
@MapperScan("com.hadoken.framework.stats.analysis.core.mapper") // 扫描Mapper接口
public class AnalyticsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public QueryBuilder queryBuilder() {
        return new DefaultQueryBuilder();
    }

    @Bean
    @ConditionalOnMissingBean
    public ChartDataTransformer chartDataTransformer() {
        return new ChartDataTransformer();
    }

    @Bean
    @ConditionalOnMissingBean
    public GenericStatisticsService genericStatisticsService(
            GenericMetricMapper genericMetricMapper,
            QueryBuilder queryBuilder,
            ChartDataTransformer chartDataTransformer) {
        return new GenericStatisticsService(genericMetricMapper, queryBuilder, chartDataTransformer);
    }

    @Configuration
    @ConditionalOnProperty(prefix = "analytics.starter.api", name = "enabled", havingValue = "true")
    public static class WebConfig {

        @Bean
        public GenericStatsController genericStatsController(GenericStatisticsService genericStatisticsService) {
            return new GenericStatsController(genericStatisticsService);
        }
    }
}