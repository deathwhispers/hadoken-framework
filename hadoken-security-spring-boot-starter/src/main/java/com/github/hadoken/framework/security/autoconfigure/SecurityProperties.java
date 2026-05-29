package com.github.hadoken.framework.security.autoconfigure;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties(prefix = "hadoken.security")
@Validated
@Data
public class SecurityProperties {

    /**
     * HTTP 请求时，访问令牌的请求 Header
     */
    @NotEmpty(message = "Token Header 不能为空")
    private String tokenHeader;

    /**
     * Token 过期时间,分钟
     */
    @NotNull(message = "Token 过期时间不能为空")
    private Duration tokenTimeout;

    /**
     * Token 秘钥
     */
    @NotEmpty(message = "Token 秘钥不能为空")
    private String tokenSecret;

    /**
     * Session 过期时间
     * <p>
     * 当 User 用户超过当前时间未操作，则 Session 会过期
     */
    @NotNull(message = "Session 过期时间不能为空")
    private Duration sessionTimeout;

    /**
     * mock 模式的开关
     */
    @NotNull(message = "mock 模式的开关不能为空")
    private Boolean mockEnable;

    /**
     * mock 模式的秘钥
     * <p>
     * 注意：只有当 mockEnable 为 true 时才需要配置此属性。
     * 必须配置一个安全的秘钥，不能使用默认值。
     */
    @NotEmpty(message = "mock模式的秘钥不能为空")
    private String mockSecret;

    /**
     * 验证配置完整性
     *
     * @throws IllegalStateException 如果必需配置缺失
     */
    public void validate() {
        if (Boolean.TRUE.equals(mockEnable)) {
            if (mockSecret == null || mockSecret.trim().isEmpty()) {
                throw new IllegalStateException(
                    "当 hadoken.security.mock-enable 为 true 时，必须配置 hadoken.security.mock-secret"
                );
            }
            // 简单安全检查：避免使用常见弱密码
            if ("123456".equals(mockSecret) || "password".equals(mockSecret) ||
                "admin".equals(mockSecret) || "test".equals(mockSecret)) {
                throw new IllegalStateException(
                    "mock-secret 不能使用常见弱密码，请设置一个安全的秘钥"
                );
            }
        }
    }

}
