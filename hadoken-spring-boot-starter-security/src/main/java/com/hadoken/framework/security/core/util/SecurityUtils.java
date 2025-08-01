package com.hadoken.framework.security.core.util;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import com.hadoken.common.exception.BadRequestException;
import com.hadoken.framework.security.core.LoginUser;
import com.hadoken.framework.security.core.enums.DataScopeEnum;
import com.hadoken.framework.web.core.util.WebUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * 安全服务工具类
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/03/01 18:15
 */
public class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * 从请求中，获得认证 Token
     *
     * @param request 请求
     * @param header  认证 Token 对应的 Header 名字
     * @return 认证 Token
     */
    public static String obtainAuthorization(HttpServletRequest request, String header) {
        String authorization = request.getHeader(header);
        if (!StringUtils.hasText(authorization)) {
            return null;
        }
        int index = authorization.indexOf("Bearer ");

        // 未找到
        if (index == -1) {
            return null;
        }
        return authorization.substring(index + 7).trim();
    }

    /**
     * 获得当前认证信息
     *
     * @return 认证信息
     */
    public static Authentication getAuthentication() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            return null;
        }
        return context.getAuthentication();
    }

    /**
     * 获取当前用户
     *
     * @return 当前用户
     */
    @Nullable
    public static LoginUser getLoginUser() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            throw new BadRequestException(HttpStatus.UNAUTHORIZED, "当前登录状态过期");
        }
        if (authentication.getPrincipal() instanceof LoginUser) {
            return (LoginUser) authentication.getPrincipal();
        }
        throw new BadRequestException(HttpStatus.UNAUTHORIZED, "找不到当前登录的信息");
    }

    /**
     * 获得当前用户的编号，从上下文中
     *
     * @return 用户编号
     */
    @Nullable
    public static Long getLoginUserId() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getId() : null;
    }

    /**
     * 获得当前用户的角色编号数组
     *
     * @return 角色编号数组
     */
    @Nullable
    public static Set<Long> getLoginUserRoleIds() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getRoleIds() : null;
    }

    /**
     * 设置当前用户
     *
     * @param loginUser 登录用户
     * @param request   请求
     */
    public static void setLoginUser(LoginUser loginUser, HttpServletRequest request) {

        // 创建 Authentication，并设置到上下文
        Authentication authentication = buildAuthentication(loginUser, request);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 额外设置到 request 中，用于 ApiAccessLogFilter 可以获取到用户编号；
        // 原因是，Spring Security 的 Filter 在 ApiAccessLogFilter 后面，在它记录访问日志时，线上上下文已经没有用户编号等信息
        WebUtils.setLoginUserId(request, loginUser.getId());
        WebUtils.setLoginUserType(request, loginUser.getUserType());
    }

    private static Authentication buildAuthentication(LoginUser loginUser, HttpServletRequest request) {

        // 创建 UsernamePasswordAuthenticationToken 对象
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authenticationToken;
    }


    /**
     * 获取当前用户的数据权限
     *
     * @return /
     */
    public static List<Long> getCurrentUserDataScope() {
        UserDetails userDetails = getLoginUser();
        JSONArray array = JSONUtil.parseArray(new JSONObject(userDetails).get("dataScopes"));
        return JSONUtil.toList(array, Long.class);
    }

    /**
     * 获取数据权限级别
     *
     * @return 级别
     */
    public static Integer getDataScopeType() {
        List<Long> dataScopes = getCurrentUserDataScope();
        if (dataScopes.size() != 0) {
            return DataScopeEnum.SELF.getScope();
        }
        return DataScopeEnum.ALL.getScope();
    }

    /**
     * 获取系统用户名称
     *
     * @return 系统用户名称
     */
    public static String getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new BadRequestException(HttpStatus.UNAUTHORIZED, "当前登录状态过期");
        }
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getUsername();
        }
        throw new BadRequestException(HttpStatus.UNAUTHORIZED, "找不到当前登录的信息");
    }
}
