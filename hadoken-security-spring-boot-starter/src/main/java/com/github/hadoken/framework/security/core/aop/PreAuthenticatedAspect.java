package com.github.hadoken.framework.security.core.aop;

import com.github.hadoken.common.exception.util.HadokenServiceExceptionUtil;
import com.github.hadoken.framework.security.core.annotations.PreAuthenticated;
import com.github.hadoken.framework.security.core.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import static com.github.hadoken.common.enums.GlobalErrorCodeConstants.UNAUTHORIZED;


/**
 * @author yanggj
 * @version 1.0.0
 * @date 2022/03/01 18:14
 */
@Aspect
@Slf4j
public class PreAuthenticatedAspect {

    @Around("@annotation(preAuthenticated)")
    public Object around(ProceedingJoinPoint joinPoint, PreAuthenticated preAuthenticated) throws Throwable {
        if (SecurityUtils.getLoginUser() == null) {
            throw HadokenServiceExceptionUtil.exception(UNAUTHORIZED);
        }
        return joinPoint.proceed();
    }
}
