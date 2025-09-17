package com.hadoken.framework.web.apilog.core.filter;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hadoken.common.util.monitor.TracerUtils;
import com.hadoken.framework.web.apilog.core.service.ApiAccessLogService;
import com.hadoken.framework.web.apilog.core.service.dto.ApiAccessLogDTO;
import com.hadoken.framework.web.mvc.core.util.ServletUtils;
import com.hadoken.framework.web.mvc.core.util.WebUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import static com.hadoken.common.exception.enums.GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR;

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

    private final String applicationName;
    private final ApiAccessLogService apiAccessLogService;

    public ApiAccessLogFilter(String applicationName, ApiAccessLogService apiAccessLogService) {
        this.applicationName = applicationName;
        this.apiAccessLogService = apiAccessLogService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        // 获得开始时间
        LocalDateTime beginTime = LocalDateTime.now();

        // 提前获得参数，避免 XssFilter 过滤处理
        Map<String, String> queryString = ServletUtils.getParamMap(request);
        String requestBody = ServletUtils.isJsonRequest(request) ? ServletUtils.getBody(request) : null;

        // 包装 response 以便读取响应体
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        Exception exception = null;
        try {
            // 继续过滤器，传入 wrapper
            filterChain.doFilter(request, responseWrapper);
        } catch (Exception ex) {
            exception = ex;
            log.error("[API_LOG] uri={}, error={}", request.getRequestURI(), ex.getMessage(), ex);
            responseWrapper.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw ex;
        } finally {
            String responsePayload = getResponsePayload(responseWrapper);
            createApiAccessLog(request, beginTime, queryString, requestBody, responsePayload, exception);

            // 写回响应
            responseWrapper.copyBodyToResponse();
        }
    }

    private void createApiAccessLog(HttpServletRequest request,
                                    LocalDateTime beginTime,
                                    Map<String, String> queryString,
                                    String requestBody,
                                    String responsePayload,
                                    Exception ex) {
        ApiAccessLogDTO accessLog = new ApiAccessLogDTO();
        try {
            this.buildApiAccessLogDTO(accessLog, request, beginTime, queryString, requestBody, responsePayload, ex);
            apiAccessLogService.createApiAccessLogAsync(accessLog);
        } catch (Throwable th) {
            log.error("createApiAccessLog >> url:{} log:{} 发生异常", request.getRequestURI(), JSONUtil.toJsonStr(accessLog), th);
        }
    }

    private void buildApiAccessLogDTO(ApiAccessLogDTO accessLog,
                                      HttpServletRequest request,
                                      LocalDateTime beginTime,
                                      Map<String, String> queryString,
                                      String requestBody,
                                      String responsePayload,
                                      Exception ex) {

        accessLog.setUserId(WebUtils.getLoginUserId());
        Integer resultCode;
        String resultMsg;
        if (ex != null) {
            resultCode = INTERNAL_SERVER_ERROR.code();
            resultMsg = ExceptionUtil.getRootCauseMessage(ex);
        } else if (StringUtils.hasText(responsePayload)) {
            try {
                JSONObject json = JSONUtil.parseObj(responsePayload);
                resultCode = json.getInt("code", INTERNAL_SERVER_ERROR.code());
                resultMsg = json.getStr("msg", "未知错误");
            } catch (Exception parseEx) {
                log.warn("[buildApiAccessLogDTO] 解析响应体失败: {}", responsePayload, parseEx);
                resultCode = INTERNAL_SERVER_ERROR.code();
                resultMsg = "响应体解析失败";
            }
        } else {
            resultCode = INTERNAL_SERVER_ERROR.code();
            resultMsg = INTERNAL_SERVER_ERROR.msg();
        }

        accessLog.setResultCode(resultCode);
        accessLog.setResultMsg(resultMsg);

        accessLog.setResponsePayload(truncate(responsePayload));

        accessLog.setTraceId(TracerUtils.getTraceId());
        accessLog.setApplicationName(applicationName);
        accessLog.setRequestUrl(request.getRequestURI());
        Map<String, Object> requestParams = MapUtil.<String, Object>builder()
                .put("query", queryString != null ? queryString : Collections.emptyMap())
                .put("body", truncate(requestBody)).build();
        accessLog.setRequestParams(JSONUtil.toJsonStr(requestParams));
        accessLog.setRequestMethod(request.getMethod());
        accessLog.setUserIp(ServletUtils.getClientIP());

        // 持续时间
        accessLog.setBeginTime(beginTime);
        accessLog.setEndTime(LocalDateTime.now());
        long durationMillis = Duration.between(accessLog.getBeginTime(), accessLog.getEndTime()).toMillis();
        accessLog.setDuration((int) Math.max(0, durationMillis));
    }


    private String getResponsePayload(ContentCachingResponseWrapper response) {
        if (response == null) {
            return null;
        }

        byte[] responseBytes = response.getContentAsByteArray();
        if (responseBytes.length == 0) {
            return "";
        }

        Charset responseCharset = StandardCharsets.UTF_8;
        try {
            return new String(responseBytes, responseCharset);
        } catch (Exception e) {
            return String.format("Failed to decode response payload (charset: %s), error: %s",
                    responseCharset.name(), e.getMessage());
        }
    }

    /**
     * 安全截断字符串，避免 null 和超长
     */
    private String truncate(String str) {
        if (str == null) {
            return null;
        }
        if (str.length() <= 5000) {
            return str;
        }
        return str.substring(0, 5000) + "...[truncated]";
    }
}
