package com.hadoken.framework.monitor.config;

import com.hadoken.common.enums.WebFilterOrderEnum;
import com.hadoken.framework.monitor.core.aop.BizTracerAspect;
import com.hadoken.framework.monitor.core.filter.TracerFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Tracer 配置类
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/03/02 10:49
 */
@Configuration
@ConditionalOnClass({BizTracerAspect.class})
@EnableConfigurationProperties(TraceProperties.class)
@ConditionalOnProperty(prefix = "hadoken.tracer", value = "enable", matchIfMissing = true)
public class HadokenTraceAutoConfiguration {

    /**
     * 创建 TraceFilter 过滤器，响应 header 设置 traceId
     */
    @Bean
    public FilterRegistrationBean<TracerFilter> traceFilter() {
        FilterRegistrationBean<TracerFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TracerFilter());
        registrationBean.setOrder(WebFilterOrderEnum.TRACE_FILTER);
        return registrationBean;
    }

}
