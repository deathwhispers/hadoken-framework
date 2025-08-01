package com.hadoken.framework.security.core.handler;

import com.hadoken.common.result.CommonResult;
import com.hadoken.common.util.string.StringUtils;
import com.hadoken.framework.security.config.SecurityProperties;
import com.hadoken.framework.security.core.authentication.MultiUserDetailsAuthenticationProvider;
import com.hadoken.framework.security.core.util.SecurityUtils;
import com.hadoken.framework.web.core.util.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;


/**
 * 自定义退出处理器
 *
 * @author yanggj
 */
@AllArgsConstructor
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    private final SecurityProperties securityProperties;

    private final MultiUserDetailsAuthenticationProvider authenticationProvider;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        // 执行退出
        String token = SecurityUtils.obtainAuthorization(request, securityProperties.getTokenHeader());
        if (StringUtils.isNotBlank(token)) {
            authenticationProvider.logout(request, token);
        }

        // 返回成功
        ServletUtils.writeJSON(response, CommonResult.success(null));
    }

}
