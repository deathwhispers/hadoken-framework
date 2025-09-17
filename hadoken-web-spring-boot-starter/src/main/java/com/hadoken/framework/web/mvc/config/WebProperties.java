package com.hadoken.framework.web.mvc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "hadoken.web")
@Validated
@Data
public class WebProperties {

    private ApiLog apiLog = new ApiLog();

    private ExceptionProperties exception = new ExceptionProperties();

    /**
     * 跨域配置（默认开启，包含安全合理的默认值）
     */
    private CorsProperties cors = new CorsProperties();

    @Data
    public static class ApiLog {
        /**
         * 是否启用全局异常拦截（默认 false）
         */
        private boolean enabled = false;

    }

    @Data
    public static class ExceptionProperties {
        /**
         * 是否启用全局异常拦截（默认 true）
         */
        private boolean enabled = true;

    }

    @Data
    public static class CorsProperties {
        /**
         * 是否启用跨域（默认：true）
         */
        private boolean enabled = true;

        /**
         * 是否允许携带 Cookie（默认：true，需注意与 allowedOrigins 的冲突）
         */
        private boolean allowCredentials = true;

        /**
         * 允许的源模式（支持通配符，如 https://*.xxx.com；默认支持所有 HTTP/HTTPS 源）
         * 优先级高于 allowedOrigins，且兼容 allowCredentials=true
         */
        private List<String> allowedOriginPatterns = List.of("http://*", "https://*");

        /**
         * 允许的请求头（默认：*，支持所有头）
         */
        private List<String> allowedHeaders = List.of("*");

        /**
         * 允许的请求方法（默认：GET/POST/PUT/DELETE/OPTIONS，覆盖常用场景）
         */
        private List<String> allowedMethods = List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        );

        /**
         * 预检请求（OPTIONS）的缓存时间（默认：3600 秒，减少预检请求次数）
         */
        private long maxAge = 3600L;

        /**
         * 多路径跨域规则
         * 格式示例：{"api/**": {}, "admin/**": {"enabled": false}}
         */
        private List<CorsPathConfig> pathConfigs = new ArrayList<>();

        /**
         * 路径级跨域配置（支持覆盖全局配置）
         */
        @Data
        public static class CorsPathConfig {
            /**
             * 路径匹配（如 /api/**、/admin/**）
             * 默认路径：/**
             */
            private String path = "/**";

            /**
             * 该路径是否启用跨域（默认继承全局 enabled，可单独关闭）
             */
            private Boolean enabled;

            /**
             * 该路径的额外配置（未配置则继承全局的 allowedOriginPatterns/allowedMethods/allowedHeaders 等）
             */
            private List<String> allowedOriginPatterns;
            private List<String> allowedHeaders;
            private List<String> allowedMethods;
            private Long maxAge;

            // 用于 List 类型
            public <T> List<T> getInheritedList(List<T> pathValue, List<T> globalValue) {
                return !CollectionUtils.isEmpty(pathValue) ? pathValue : globalValue;
            }

            public Boolean getInheritedEnabled(Boolean globalEnabled) {
                return this.enabled != null ? this.enabled : globalEnabled;
            }

            public Long getInheritedMaxAge(Long globalMaxAge) {
                return this.maxAge != null ? this.maxAge : globalMaxAge;
            }
        }

        public void validate() {
            if (allowCredentials) {
                boolean hasValidPatterns = !CollectionUtils.isEmpty(allowedOriginPatterns) &&
                        allowedOriginPatterns.stream().noneMatch("*"::equals);

                if (!hasValidPatterns) {
                    throw new IllegalArgumentException(
                            "当 allowCredentials=true 时，需配置 allowedOriginPatterns 并且不能为 '*' "
                    );
                }
            }

            // 2. 路径配置校验（避免重复路径）
            List<String> paths = pathConfigs.stream().map(CorsPathConfig::getPath).toList();
            if (paths.size() != paths.stream().distinct().count()) {
                throw new IllegalArgumentException("跨域路径配置存在重复：" + paths);
            }
        }
    }

}