package com.hadoken.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
 * 获取 HttpServletRequest
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/4/22 10:59
 */
public class RequestHolder {

    public static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }

}
