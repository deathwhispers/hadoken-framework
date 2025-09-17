package com.hadoken.framework.web.xss.config;

import com.hadoken.common.enums.WebFilterOrderEnum;
import com.hadoken.framework.web.mvc.config.HadokenWebAutoConfiguration;
import com.hadoken.framework.web.xss.core.cleaner.JsoupXssCleaner;
import com.hadoken.framework.web.xss.core.cleaner.XssCleaner;
import com.hadoken.framework.web.xss.core.filter.XssFilter;
import com.hadoken.framework.web.xss.core.serializer.XssStringJsonDeserializer;
import jakarta.servlet.DispatcherType;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 默认不开启
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/9/14 20:16
 */
@AutoConfiguration(after = HadokenWebAutoConfiguration.class)
@EnableConfigurationProperties({XssProperties.class})
@ConditionalOnProperty(prefix = "hadoken.xss", name = "enable", havingValue = "true")
public class HadokenXssAutoConfiguration implements WebMvcConfigurer {

    /**
     * Xss 清理者
     *
     * @return XssCleaner
     */
    @Bean
    @ConditionalOnMissingBean(XssCleaner.class)
    public XssCleaner xssCleaner() {
        return new JsoupXssCleaner();
    }

    /**
     * 注册 Jackson 的序列化器，用于处理 json 类型参数的 xss 过滤
     *
     * @return Jackson2ObjectMapperBuilderCustomizer
     */
    @Bean
    @ConditionalOnMissingBean(name = "xssJacksonCustomizer")
    @ConditionalOnProperty(value = "hadoken.xss.enable", havingValue = "true")
    public Jackson2ObjectMapperBuilderCustomizer xssJacksonCustomizer(XssProperties properties,
                                                                      PathMatcher pathMatcher,
                                                                      XssCleaner xssCleaner) {
        // 在反序列化时进行 xss 过滤，可以替换使用 XssStringJsonSerializer，在序列化时进行处理
        return builder ->
                builder.deserializerByType(String.class, new XssStringJsonDeserializer(properties, pathMatcher, xssCleaner));
    }

    @Bean
    @ConditionalOnBean(XssFilter.class)
    public FilterRegistrationBean<XssFilter> xssFilter(XssProperties properties,
                                                       PathMatcher pathMatcher,
                                                       XssCleaner xssCleaner) {
        FilterRegistrationBean<XssFilter> bean = new FilterRegistrationBean<>();
        bean.setDispatcherTypes(DispatcherType.REQUEST);
        bean.setName("xssFilter");
        bean.setFilter(new XssFilter(properties, pathMatcher, xssCleaner));
        bean.setOrder(WebFilterOrderEnum.XSS_FILTER);
        return bean;
    }

}
