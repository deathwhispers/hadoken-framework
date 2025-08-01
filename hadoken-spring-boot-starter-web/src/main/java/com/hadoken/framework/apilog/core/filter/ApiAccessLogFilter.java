package com.hadoken.framework.apilog.core.filter;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import com.hadoken.common.exception.enums.GlobalErrorCodeConstants;
import com.hadoken.common.result.CommonResult;
import com.hadoken.common.util.date.DateUtil;
import com.hadoken.common.util.monitor.TracerUtils;
import com.hadoken.common.util.string.StringUtils;
import com.hadoken.framework.apilog.core.service.ApiAccessLogFrameworkService;
import com.hadoken.framework.apilog.core.service.dto.ApiAccessLogDTO;
import com.hadoken.framework.web.config.WebProperties;
import com.hadoken.framework.web.core.util.ServletUtils;
import com.hadoken.framework.web.core.util.WebUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * API 访问日志 Filter
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/03/02 9:27
 * @see OncePerRequestFilter 仅执行一次的过滤器：确保在一次请求中只通过一次filter，用来记录每次请求的日志
 */
@Slf4j
public class ApiAccessLogFilter extends OncePerRequestFilter {

    private final WebProperties webProperties;
    private final String applicationName;
    private final ApiAccessLogFrameworkService apiAccessLogFrameworkService;

    public ApiAccessLogFilter(WebProperties webProperties, String applicationName, ApiAccessLogFrameworkService apiAccessLogFrameworkService) {
        this.webProperties = webProperties;
        this.applicationName = applicationName;
        this.apiAccessLogFrameworkService = apiAccessLogFrameworkService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        // 只过滤 API 请求的地址
        return !StringUtils.startWithAny(request.getRequestURI(), webProperties.getAppApi().getPrefix(),
                webProperties.getAppApi().getPrefix());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
/*
        // 获得开始时间
        Date beginTim = new Date();

        // 提前获得参数，避免 XssFilter 过滤处理
        Map<String, String> queryString = ServletUtil.getParamMap(request);
        String requestBody = ServletUtils.isJsonRequest(request) ? ServletUtil.getBody(request) : null;

        try {

            // 继续过滤器
            filterChain.doFilter(request, response);

            // 正常执行，记录日志
            createApiAccessLog(request, beginTim, queryString, requestBody, null);
        } catch (Exception ex) {

            // 异常执行，记录日志
            createApiAccessLog(request, beginTim, queryString, requestBody, ex);
            throw ex;
        }*/
    }

    private void createApiAccessLog(HttpServletRequest request,
                                    Date beginTime,
                                    Map<String, String> queryString,
                                    String requestBody,
                                    Exception ex) {
        ApiAccessLogDTO accessLog = new ApiAccessLogDTO();
        try {
            this.buildApiAccessLogDTO(accessLog, request, beginTime, queryString, requestBody, ex);
            apiAccessLogFrameworkService.createApiAccessLogAsync(accessLog);
        } catch (Throwable th) {
            log.error("[createApiAccessLog][url({}) log({}) 发生异常]", request.getRequestURI(), JSONUtil.toJsonStr(accessLog), th);
        }
    }

    private void buildApiAccessLogDTO(ApiAccessLogDTO accessLog,
                                      HttpServletRequest request,
                                      Date beginTime,
                                      Map<String, String> queryString,
                                      String requestBody,
                                      Exception ex) {

        // 处理用户信息
        accessLog.setUserId(WebUtils.getLoginUserId(request));
        accessLog.setUserType(WebUtils.getLoginUserType(request));

        // 设置访问结果
        CommonResult<?> result = WebUtils.getCommonResult(request);
        if (result != null) {
            accessLog.setResultCode(result.getCode());
            accessLog.setResultMsg(result.getMsg());
        } else if (ex != null) {
            accessLog.setResultCode(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode());
            accessLog.setResultMsg(ExceptionUtil.getRootCauseMessage(ex));
        } else {
            accessLog.setResultCode(0);
            accessLog.setResultMsg("");
        }

        // 设置其它字段
        accessLog.setTraceId(TracerUtils.getTraceId());
        accessLog.setApplicationName(applicationName);
        accessLog.setRequestUrl(request.getRequestURI());
        Map<String, Object> requestParams = MapUtil.<String, Object>builder().put("query", queryString).put("body", requestBody).build();
        accessLog.setRequestParams(JSONUtil.toJsonStr(requestParams));
        accessLog.setRequestMethod(request.getMethod());
        accessLog.setUserAgent(ServletUtils.getUserAgent(request));
//        accessLog.setUserIp(ServletUtil.getClientIP(request));

        // 持续时间
        accessLog.setBeginTime(beginTime);
        accessLog.setEndTime(new Date());
        accessLog.setDuration((int) DateUtil.diff(accessLog.getEndTime(), accessLog.getBeginTime()));
    }
}
