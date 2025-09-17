package com.hadoken.framework.web.springdoc.config;

import com.github.xiaoymin.knife4j.spring.configuration.Knife4jProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Swagger 自动配置类，基于 OpenAPI + Springdoc 实现。
 */
@AutoConfiguration
@ConditionalOnClass({OpenAPI.class})
@EnableConfigurationProperties({SwaggerProperties.class, Knife4jProperties.class})
@ConditionalOnProperty(prefix = "springdoc.api-docs", name = "enabled", havingValue = "true", matchIfMissing = true)
@Import(Knife4jOpenApiCustomizer.class)
public class HadokenSwaggerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public OpenAPI defaultOpenAPI(Environment env) {
        Map<String, SecurityScheme> securitySchemas = buildSecuritySchemes();
        // 组装OpenAPI基础信息
        OpenAPI openAPI = new OpenAPI()
                .info(buildInfo(env))
                .components(new Components().securitySchemes(securitySchemas))
                .addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION));
        securitySchemas.keySet().forEach(key -> openAPI.addSecurityItem(new SecurityRequirement().addList(key)));
        return openAPI;
    }


    /**
     * API 摘要信息
     */
    private Info buildInfo(Environment env) {
        String title = env.getProperty("springdoc.info.title", "系统API文档");
        String description = env.getProperty("springdoc.info.description", "接口文档自动生成");
        String version = env.getProperty("springdoc.info.version", "1.0.0");
        String contactName = env.getProperty("springdoc.info.contact.name", "瑞华赢");
        String contactEmail = env.getProperty("springdoc.info.contact.email", "dev@example.com");
        String contactUrl = env.getProperty("springdoc.info.contact.url", "");
        String licenseName = env.getProperty("springdoc.info.license.name", "Apache 2.0");
        String licenseUrl = env.getProperty("springdoc.info.license.url", "https://www.apache.org/licenses/LICENSE-2.0.html");

        // 构建联系人和许可证信息（使用配置值或默认值）
        Contact contact = new Contact()
                .name(contactName)
                .email(contactEmail)
                .url(contactUrl);

        License license = new License()
                .name(licenseName)
                .url(licenseUrl);
        return new Info()
                .title(title)
                .description(description)
                .version(version)
                .contact(contact)
                .license(license);
    }


    /**
     * 安全模式，这里配置通过请求头 Authorization 传递 token 参数
     */
    private Map<String, SecurityScheme> buildSecuritySchemes() {
        Map<String, SecurityScheme> securitySchemes = new HashMap<>();
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .name(HttpHeaders.AUTHORIZATION)
                .in(SecurityScheme.In.HEADER)
                .scheme("bearer")
                .bearerFormat("JWT");
        securitySchemes.put(HttpHeaders.AUTHORIZATION, securityScheme);
        return securitySchemes;
    }

    /**
     * API 分组配置（支持主项目通过 SwaggerProperties 自定义分组）
     */
    @Bean
    @ConditionalOnMissingBean
    public List<GroupedOpenApi> defaultApiGroups(SwaggerProperties properties) {
        // 无自定义分组时，默认创建 "default" 分组（匹配所有接口）
        if (properties.getGroups().isEmpty()) {
            return List.of(GroupedOpenApi.builder()
                    .group("default")
                    .pathsToMatch("/**")
                    .build());
        }

        // 有自定义分组时，按配置生成分组
        return properties.getGroups().stream()
                .map(group -> GroupedOpenApi.builder()
                        .group(group.getName())
                        .pathsToMatch(group.getPathsToMatch().toArray(new String[0]))
                        .build())
                .collect(Collectors.toList());
    }


    /**
     * 接口排序（默认启用）
     */
    @Bean
    public OpenApiCustomizer operationSortCustomizer(SwaggerProperties properties) {
        return openApi -> {
            if (properties.isEnableOperationSort() && openApi.getPaths() != null) {
                openApi.setPaths((io.swagger.v3.oas.models.Paths) openApi.getPaths().entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (e1, e2) -> e1,
                                LinkedHashMap::new
                        )));
            }
        };
    }

    /**
     * 参数过滤（隐藏指定参数）
     */
    @Bean
    public OpenApiCustomizer parameterFilterCustomizer(SwaggerProperties properties) {
        return openApi -> {
            if (openApi.getPaths() == null || properties.getIgnoredParameters().isEmpty()) return;

            openApi.getPaths().values().forEach(pathItem ->
                    pathItem.readOperations().forEach(operation ->
                            operation.getParameters().removeIf(p ->
                                    properties.getIgnoredParameters().contains(p.getName()))));
        };
    }


}

