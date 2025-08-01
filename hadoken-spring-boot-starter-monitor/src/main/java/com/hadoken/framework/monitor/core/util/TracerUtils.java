package com.hadoken.framework.monitor.core.util;

import io.opentracing.Span;
import io.opentracing.tag.Tags;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Trace 工具类
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/03/02 10:51
 */
public class TracerUtils {

    /**
     * 将异常记录到 Span 中，参考自 com.aliyuncs.utils.TraceUtils
     *
     * @param throwable 异常
     * @param span      Span
     */
    public static void onError(Throwable throwable, Span span) {
        Tags.ERROR.set(span, Boolean.TRUE);
        if (throwable != null) {
            span.log(errorLogs(throwable));
        }
    }

    private static Map<String, Object> errorLogs(Throwable throwable) {
        Map<String, Object> errorLogs = new HashMap<String, Object>(10);
        errorLogs.put("event", Tags.ERROR.getKey());
        errorLogs.put("error.object", throwable);
        errorLogs.put("error.kind", throwable.getClass().getName());
        String message = throwable.getCause() != null ? throwable.getCause().getMessage() : throwable.getMessage();
        if (message != null) {
            errorLogs.put("message", message);
        }
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        errorLogs.put("stack", sw.toString());
        return errorLogs;
    }

}
