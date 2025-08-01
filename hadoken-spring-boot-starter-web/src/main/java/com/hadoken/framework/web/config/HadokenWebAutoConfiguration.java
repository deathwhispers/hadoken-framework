package com.hadoken.framework.web.config;

import com.hadoken.common.enums.WebFilterOrderEnum;
import com.hadoken.framework.apilog.core.service.ApiErrorLogFrameworkService;
import com.hadoken.framework.web.core.filter.CacheRequestBodyFilter;
import com.hadoken.framework.web.core.filter.XssFilter;
import com.hadoken.framework.web.core.handler.GlobalExceptionHandler;
import jakarta.annotation.Resource;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author yanggj
 * @version 1.0.0
 * @date 2022/03/01 17:29
 */
@Configuration
@EnableConfigurationProperties({WebProperties.class, XssProperties.class})
public class HadokenWebAutoConfiguration implements WebMvcConfigurer {

    @Resource
    private WebProperties webProperties;

    /**
     * 应用名
     */
    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurePathMatch(configurer, webProperties.getAdminApi());
        configurePathMatch(configurer, webProperties.getAppApi());
    }

    /**
     * 设置 API 前缀，仅仅匹配 controller 包下的
     *
     * @param configurer 配置
     * @param api        API 配置
     */
    private void configurePathMatch(PathMatchConfigurer configurer, WebProperties.Api api) {
        AntPathMatcher antPathMatcher = new AntPathMatcher(".");
        configurer.addPathPrefix(api.getPrefix(), clazz -> clazz.isAnnotationPresent(RestController.class)
                && antPathMatcher.match(api.getController(), clazz.getPackage().getName())); // 仅仅匹配 controller 包
    }

//    @Bean
//    public GlobalExceptionHandler globalExceptionHandler(ApiErrorLogFrameworkService ApiErrorLogFrameworkService) {
//        return new GlobalExceptionHandler(applicationName, ApiErrorLogFrameworkService);
//    }

    // ========== Filter 相关 ==========

    /**
     * 创建 CorsFilter Bean，解决跨域问题
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterBean() {

        // 创建 CorsConfiguration 对象
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);

        // 设置访问源地址
        config.addAllowedOriginPattern("*");

        // 设置访问源请求头
        config.addAllowedHeader("*");

        // 设置访问源请求方法
        config.addAllowedMethod("*");

        // 创建 UrlBasedCorsConfigurationSource 对象
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // 对接口配置跨域设置
        source.registerCorsConfiguration("/**", config);
        return createFilterBean(new CorsFilter(source), WebFilterOrderEnum.CORS_FILTER);
    }

    /**
     * 创建 RequestBodyCacheFilter Bean，可重复读取请求内容
     */
    @Bean
    public FilterRegistrationBean<CacheRequestBodyFilter> requestBodyCacheFilter() {
        return createFilterBean(new CacheRequestBodyFilter(), WebFilterOrderEnum.REQUEST_BODY_CACHE_FILTER);
    }

    /**
     * 创建 XssFilter Bean，解决 Xss 安全问题
     */
//    @Bean
//    public FilterRegistrationBean<XssFilter> xssFilter(XssProperties properties, PathMatcher pathMatcher) {
//        return createFilterBean(new XssFilter(properties, pathMatcher), WebFilterOrderEnum.XSS_FILTER);
//    }

    private static <T extends Filter> FilterRegistrationBean<T> createFilterBean(T filter, Integer order) {
        FilterRegistrationBean<T> bean = new FilterRegistrationBean<>(filter);
        bean.setOrder(order);
        return bean;
    }

}
