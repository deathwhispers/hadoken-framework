package com.hadoken.framework.security.config;

import com.hadoken.common.util.string.StringUtils;
import com.hadoken.framework.security.core.authentication.MultiUserDetailsAuthenticationProvider;
import com.hadoken.framework.security.core.filter.JWTAuthenticationTokenFilter;
import com.hadoken.framework.web.config.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * 自定义的 Spring Security 配置适配器实现
 *
 * @author yanggj
 */
@Configuration
@EnableWebSecurity
@Order(10)
public class HadokenWebSecurityConfig {

    private final WebProperties webProperties;
    private final MultiUserDetailsAuthenticationProvider authenticationProvider;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;
    private final LogoutSuccessHandler logoutSuccessHandler;
    private final JWTAuthenticationTokenFilter authenticationTokenFilter;
    private final List<AuthorizeRequestsCustomizer> authorizeRequestsCustomizers;

    public HadokenWebSecurityConfig(
            WebProperties webProperties,
            MultiUserDetailsAuthenticationProvider authenticationProvider,
            AuthenticationEntryPoint authenticationEntryPoint,
            AccessDeniedHandler accessDeniedHandler,
            LogoutSuccessHandler logoutSuccessHandler,
            JWTAuthenticationTokenFilter authenticationTokenFilter,
            List<AuthorizeRequestsCustomizer> authorizeRequestsCustomizers) {
        this.webProperties = webProperties;
        this.authenticationProvider = authenticationProvider;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.logoutSuccessHandler = logoutSuccessHandler;
        this.authenticationTokenFilter = authenticationTokenFilter;
        this.authorizeRequestsCustomizers = authorizeRequestsCustomizers;
    }

    /**
     * 暴露 AuthenticationManager 为 Bean
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 注册 AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        return authenticationProvider;
    }

    /**
     * 配置 SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // 启用 CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 添加 JWT 过滤器
                .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)

                // 异常处理
                .exceptionHandling(ex -> {
                    ex.authenticationEntryPoint(authenticationEntryPoint);
                    ex.accessDeniedHandler(accessDeniedHandler);
                })

                // 🔥 新方式：直接配置 headers，禁用 frameOptions
                .headers(headers -> headers
                                .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable // 禁用 X-Frame-Options
                                )
                        // 可选：其他 header 配置
                        // .contentSecurityPolicy(csp -> csp.policyDirectives("..."))
                )

                // 无状态会话
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 授权规则
                .authorizeHttpRequests(authz -> authz
                        // 静态资源、Swagger、Druid、OPTIONS 等放行
                        .requestMatchers(HttpMethod.GET,
                                "/*.html",
                                "/**/*.html",
                                "/**/*.css",
                                "/**/*.js",
                                "/ws/**",
                                "/wss/**"
                        ).permitAll()
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/doc.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/v3/api-docs"
                        ).permitAll()
                        .requestMatchers("/druid/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/**").permitAll()

                        // 兜底：其余请求必须认证
                        .anyRequest().authenticated())

                // 登出配置
                .logout(logout -> logout
                        .logoutSuccessHandler(logoutSuccessHandler)
                        .logoutRequestMatcher(request ->
                                StringUtils.equalsAny(request.getRequestURI(),
                                        buildAdminApi("/system/logout"),
                                        buildAppApi("/member/logout")
                                )
                        ));

        return http.build();
    }

    /**
     * CORS 配置（可选）
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*"); // Spring Security 6 推荐使用 allowedOriginPattern
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private String buildAdminApi(String url) {
        return webProperties.getAdminApi().getPrefix() + url;
    }

    private String buildAppApi(String url) {
        return webProperties.getAppApi().getPrefix() + url;
    }
}
