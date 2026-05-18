package com.github.hadoken.framework.security.autoconfigure;

import com.github.hadoken.framework.web.mvc.autoconfigure.WebProperties;
import jakarta.annotation.Resource;
import org.springframework.core.Ordered;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

/**
 * <p>
 * 自定义的 URL 的安全配置
 * 目的：每个 Maven Module 可以自定义规则！
 * </p>
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/03/02 9:38
 */
public abstract class AuthorizeRequestsCustomizer
        implements Customizer<ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry>, Ordered {

    @Resource
    private WebProperties webProperties;

    @Override
    public int getOrder() {
        return 0;
    }
}
