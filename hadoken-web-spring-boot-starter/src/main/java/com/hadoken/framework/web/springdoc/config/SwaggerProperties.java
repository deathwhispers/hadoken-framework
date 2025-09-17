package com.hadoken.framework.web.springdoc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger 配置属性
 */
@ConfigurationProperties("hadoken.springdoc")
@Data
public class SwaggerProperties {

    /**
     * 自定义API分组（默认自动创建"default"分组）
     */
    private List<ApiGroup> groups = new ArrayList<>();


    /**
     * 是否启用接口排序（默认：true）
     */
    private boolean enableOperationSort = true;

    /**
     * 是否启用参数过滤（默认：true）
     */
    private boolean enableParameterFilter = true;

    /**
     * 文档中需要忽略的参数名
     * 需要隐藏的参数名（如 token、sign 等，默认空）
     */
    private List<String> ignoredParameters = new ArrayList<>();

    @Data
    public static class ApiGroup {
        private String name;
        private List<String> pathsToMatch = new ArrayList<>();
        private String title;
        private String description;
    }

}
