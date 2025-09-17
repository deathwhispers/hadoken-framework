package com.hadoken.framework.web.apilog.config;

import com.hadoken.common.enums.WebFilterOrderEnum;
import com.hadoken.framework.web.apilog.core.filter.ApiAccessLogFilter;
import com.hadoken.framework.web.apilog.core.service.ApiAccessLogService;
import com.hadoken.framework.web.apilog.core.service.DefaultApiAccessLogService;
import com.hadoken.framework.web.mvc.config.HadokenWebAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * Api 日志自动配置类
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/03/02 9:19
 */
@Slf4j
@AutoConfiguration(after = HadokenWebAutoConfiguration.class)
@ConditionalOnProperty(prefix = "hadoken.web.api-log", name = "enabled", havingValue = "true")
public class HadokenApiLogAutoConfiguration {


    /**
     * 创建 ApiAccessLogFilter Bean，记录 API 请求日志
     */
    @Bean
    public FilterRegistrationBean<ApiAccessLogFilter> apiAccessLogFilter(@Value("${spring.application.name}") String applicationName,
                                                                         ApiAccessLogService apiAccessLogService) {
        ApiAccessLogFilter filter = new ApiAccessLogFilter(applicationName, apiAccessLogService);
        FilterRegistrationBean<ApiAccessLogFilter> bean = new FilterRegistrationBean<>(filter);
        bean.setOrder(WebFilterOrderEnum.API_ACCESS_LOG_FILTER);
        return bean;
    }

    /**
     * 创建默认的API访问日志服务实现
     * 当用户没有自定义实现时使用这个默认实现
     */
    @Bean
    @ConditionalOnMissingBean
    public ApiAccessLogService defaultApiAccessLogService() {
        // 默认实现可以是空实现，或者简单打印日志
        return new DefaultApiAccessLogService();
    }

}
