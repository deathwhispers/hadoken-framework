package com.hadoken.framework.monitor.core.aop;

import cn.hutool.core.map.MapUtil;
import com.hadoken.common.util.spring.SpringExpressionUtils;
import com.hadoken.common.util.string.StringUtils;
import com.hadoken.framework.monitor.core.annotation.BizTracer;
import com.hadoken.framework.monitor.core.util.TracerUtils;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.Map;

import static java.util.Arrays.asList;

/**
 * 链路追踪 切面 记录业务链路
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/03/02 10:50
 */
@Aspect
@Slf4j
public class BizTracerAspect {
    private static final String BIZ_OPERATION_NAME_PREFIX = "Biz/";

    private final Tracer tracer;

    public BizTracerAspect(Tracer tracer) {
        this.tracer = tracer;
    }

    @Around(value = "@annotation(trace)")
    public Object around(ProceedingJoinPoint joinPoint, BizTracer trace) throws Throwable {

        // 创建 span
        String operationName = getOperationName(joinPoint, trace);
        Span span = tracer.buildSpan(operationName)
                .withTag(Tags.COMPONENT.getKey(), "biz")
                .start();
        try {

            // 执行原有方法
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            TracerUtils.onError(throwable, span);
            throw throwable;
        } finally {

            // 设置 Span 的 biz 属性
            setBizTag(span, joinPoint, trace);

            // 完成 Span
            span.finish();
        }
    }

    private String getOperationName(ProceedingJoinPoint joinPoint, BizTracer trace) {

        // 自定义操作名
        if (StringUtils.isNotEmpty(trace.operationName())) {
            return BIZ_OPERATION_NAME_PREFIX + trace.operationName();
        }

        // 默认操作名，使用方法名
        return BIZ_OPERATION_NAME_PREFIX
                + joinPoint.getSignature().getDeclaringType().getSimpleName()
                + "/" + joinPoint.getSignature().getName();
    }

    private void setBizTag(Span span, ProceedingJoinPoint joinPoint, BizTracer trace) {
        try {
            Map<String, Object> result = SpringExpressionUtils.parseExpressions(joinPoint, asList(trace.type(), trace.id()));
            span.setTag(BizTracer.TYPE_TAG, MapUtil.getStr(result, trace.type()));
            span.setTag(BizTracer.ID_TAG, MapUtil.getStr(result, trace.id()));
        } catch (Exception ex) {
            log.error("[setBizTag][解析 bizType 与 bizId 发生异常]", ex);
        }
    }

}
