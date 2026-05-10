package com.hadoken.framework.web.encrypt.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@AutoConfiguration
@Slf4j
@EnableConfigurationProperties(ApiEncryptProperties.class)
@ConditionalOnProperty(prefix = "hadoken.api-encrypt", name = "enable", havingValue = "true")
public class HadokenApiEncryptAutoConfiguration {

//    @Bean
//    public FilterRegistrationBean<ApiEncryptFilter> apiEncryptFilter(WebProperties webProperties,
//                                                                     ApiEncryptProperties apiEncryptProperties,
//                                                                     RequestMappingHandlerMapping requestMappingHandlerMapping,
//                                                                     GlobalExceptionHandler globalExceptionHandler) {
//        ApiEncryptFilter filter = new ApiEncryptFilter(webProperties, apiEncryptProperties,
//                requestMappingHandlerMapping, globalExceptionHandler);
//        return createFilterBean(filter, WebFilterOrderEnum.API_ENCRYPT_FILTER);
//
//    }

}
