package com.hadoken.framework.web.mvc.config;

import com.hadoken.common.enums.WebFilterOrderEnum;
import com.hadoken.framework.web.apilog.core.service.ApiErrorLogService;
import com.hadoken.framework.web.apilog.core.service.DefaultApiErrorLogService;
import com.hadoken.framework.web.mvc.core.filter.CacheRequestBodyFilter;
import com.hadoken.framework.web.mvc.core.handler.GlobalExceptionHandler;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author yanggj
 * @version 1.0.0
 * @date 2022/03/01 17:29
 */
@Configuration
@EnableConfigurationProperties({WebProperties.class})
public class HadokenWebAutoConfiguration implements WebMvcConfigurer {

    @Resource
    private WebProperties webProperties;

    @Value("${spring.application.name:unknown-application}")
    private String applicationName;

    @Bean
    @ConditionalOnProperty(prefix = "hadoken.web.exception", name = "enabled", havingValue = "true", matchIfMissing = true)
    public GlobalExceptionHandler globalExceptionHandler(ApiErrorLogService apiErrorLogService) {
        // 配置启用时，创建全局异常处理器 Bean
        return new GlobalExceptionHandler(applicationName, apiErrorLogService);
    }

    @Bean
    @ConditionalOnMissingBean
    public ApiErrorLogService defaultApiErrorLogService() {
        // 默认实现可以是空实现，或者简单打印日志
        return new DefaultApiErrorLogService();
    }

    /**
     * 创建 CorsFilter Bean，解决跨域问题
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterBean() {
        WebProperties.CorsProperties globalCors = webProperties.getCors();

        if (!globalCors.isEnabled()) {
            FilterRegistrationBean<CorsFilter> emptyBean = new FilterRegistrationBean<>();
            emptyBean.setEnabled(false);
            return emptyBean;
        }

        globalCors.validate();

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        List<WebProperties.CorsProperties.CorsPathConfig> pathConfigs = globalCors.getPathConfigs();

        if (pathConfigs.isEmpty()) {
            source.registerCorsConfiguration("/**", buildCorsConfig(globalCors, null));
        } else {
            for (WebProperties.CorsProperties.CorsPathConfig pathConfig : pathConfigs) {
                // 路径级开关关闭：注册空配置（禁止跨域）
                if (!pathConfig.getInheritedEnabled(globalCors.isEnabled())) {
                    source.registerCorsConfiguration(pathConfig.getPath(), new CorsConfiguration());
                    continue;
                }
                // 构建路径级的 CorsConfiguration（继承全局+覆盖路径配置）
                CorsConfiguration pathCorsConfig = buildCorsConfig(globalCors, pathConfig);
                source.registerCorsConfiguration(pathConfig.getPath(), pathCorsConfig);
            }
        }

        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(WebFilterOrderEnum.CORS_FILTER);
        return bean;
    }

    /**
     * 构建 CorsConfiguration（支持路径配置继承全局配置）
     */
    private CorsConfiguration buildCorsConfig(WebProperties.CorsProperties globalConfig,
                                              WebProperties.CorsProperties.CorsPathConfig pathConfig) {
        CorsConfiguration config = new CorsConfiguration();

        // 基础配置：继承全局，路径配置优先
        config.setAllowCredentials(globalConfig.isAllowCredentials());
        if (pathConfig != null) {
            config.setAllowedOriginPatterns(pathConfig.getInheritedList(pathConfig.getAllowedOriginPatterns(), globalConfig.getAllowedOriginPatterns()));
            config.setAllowedHeaders(pathConfig.getInheritedList(pathConfig.getAllowedHeaders(), globalConfig.getAllowedHeaders()));
            config.setAllowedMethods(pathConfig.getInheritedList(pathConfig.getAllowedMethods(), globalConfig.getAllowedMethods()));
            config.setMaxAge(pathConfig.getInheritedMaxAge(globalConfig.getMaxAge()));
        } else {
            config.setAllowedOriginPatterns(globalConfig.getAllowedOriginPatterns());
            config.setAllowedHeaders(globalConfig.getAllowedHeaders());
            config.setAllowedMethods(globalConfig.getAllowedMethods());
            config.setMaxAge(globalConfig.getMaxAge());
        }
        return config;
    }

    /**
     * 创建 RequestBodyCacheFilter Bean，可重复读取请求内容
     */
    @Bean
    public FilterRegistrationBean<CacheRequestBodyFilter> requestBodyCacheFilter() {
        CacheRequestBodyFilter cacheRequestBodyFilter = new CacheRequestBodyFilter();
        FilterRegistrationBean<CacheRequestBodyFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(cacheRequestBodyFilter);
        bean.setOrder(WebFilterOrderEnum.REQUEST_BODY_CACHE_FILTER);
        return bean;
    }

    /**
     * 创建 RestTemplate 实例
     *
     * @param restTemplateBuilder {@link RestTemplateAutoConfiguration#restTemplateBuilder}
     */
    @Bean
    @ConditionalOnMissingBean
    @LoadBalanced
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

}
