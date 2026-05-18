package com.github.hadoken.framework.security.autoconfigure;

import com.github.hadoken.framework.web.mvc.autoconfigure.WebProperties;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * 自定义的 Spring Security 配置适配器实现
 *
 * @author yanggj
 */
@Configuration
@EnableWebSecurity
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
public class HadokenWebSecurityConfigurerAdapter {

    @Resource
    private WebProperties webProperties;

    /**
     * 认证失败处理类 Bean
     */
    @Resource
    private AuthenticationEntryPoint authenticationEntryPoint;

    /**
     * 权限不够处理器 Bean
     */
    @Resource
    private AccessDeniedHandler accessDeniedHandler;

    /**
     * 退出处理类 Bean
     */
    @Resource
    private LogoutSuccessHandler logoutSuccessHandler;

}
