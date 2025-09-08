package com.hadoken.framework.web.apilog.config;

import com.hadoken.common.enums.WebFilterOrderEnum;
import com.hadoken.framework.web.apilog.core.filter.ApiAccessLogFilter;
import com.hadoken.framework.web.apilog.core.service.ApiAccessLogFrameworkService;
import com.hadoken.framework.web.mvc.config.HadokenWebAutoConfiguration;
import com.hadoken.framework.web.mvc.config.WebProperties;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Api 日志自动配置类
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/03/02 9:19
 */
@AutoConfiguration(after = HadokenWebAutoConfiguration.class)
public class HadokenApiLogAutoConfiguration implements WebMvcConfigurer {

    /**
     * 创建 ApiAccessLogFilter Bean，记录 API 请求日志
     */
    @Bean
    @ConditionalOnProperty(prefix = "hadoken.access-log", value = "enable", matchIfMissing = false)
    public FilterRegistrationBean<ApiAccessLogFilter> apiAccessLogFilter(WebProperties webProperties,
                                                                         @Value("${spring.application.name}") String applicationName,
                                                                         ApiAccessLogFrameworkService apiAccessLogFrameworkService) {
        ApiAccessLogFilter filter = new ApiAccessLogFilter(webProperties, applicationName, apiAccessLogFrameworkService);
        return createFilterBean(filter, WebFilterOrderEnum.API_ACCESS_LOG_FILTER);
    }

    private static <T extends Filter> FilterRegistrationBean<T> createFilterBean(T filter, Integer order) {
        FilterRegistrationBean<T> bean = new FilterRegistrationBean<>(filter);
        bean.setOrder(order);
        return bean;
    }

}
