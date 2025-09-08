package com.hadoken.common.exception.util;

import com.google.common.annotations.VisibleForTesting;
import com.hadoken.common.exception.ErrorCode;
import com.hadoken.common.exception.HadokenServiceException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * {@link HadokenServiceException} 工具类
 * <p>
 * 目的在于，格式化异常信息提示。
 * 考虑到 String.format 在参数不正确时会报错，因此使用 {} 作为占位符，并使用 {@link #doFormat(int, String, Object...)} 方法来格式化
 * <p>
 * 因为 {@link #MESSAGES} 里面默认是没有异常信息提示的模板的，所以需要使用方自己初始化进去。目前想到的有几种方式：
 * <p>
 * <ul>
 *   <li>1. 异常提示信息，写在枚举类中，例如说，cn.iocoder.oceans.user.api.constants.ErrorCodeEnum 类 + ServiceExceptionConfiguration</li>
 *   <li>2. 异常提示信息，写在 .properties 等等配置文件</li>
 *   <li>3. 异常提示信息，写在 Apollo 等等配置中心中，从而实现可动态刷新</li>
 *   <li>4. 异常提示信息，存储在 db 等等数据库中，从而实现可动态刷新</li>
 * </ul>
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/02/28 16:00
 */
@Slf4j
public class HadokenServiceExceptionUtil {

    /**
     * 错误码提示模板
     */
    private static final ConcurrentMap<Integer, String> MESSAGES = new ConcurrentHashMap<>();

    public static void putAll(Map<Integer, String> messages) {
        HadokenServiceExceptionUtil.MESSAGES.putAll(messages);
    }

    public static void put(Integer code, String message) {
        HadokenServiceExceptionUtil.MESSAGES.put(code, message);
    }

    public static void delete(Integer code, String message) {
        HadokenServiceExceptionUtil.MESSAGES.remove(code, message);
    }

    // ========== 和 ServiceException 的集成 ==========

    public static HadokenServiceException exception(ErrorCode errorCode) {
        String messagePattern = MESSAGES.getOrDefault(errorCode.code(), errorCode.msg());
        return exception0(errorCode.code(), messagePattern);
    }

    public static HadokenServiceException exception(ErrorCode errorCode, Object... params) {
        String messagePattern = MESSAGES.getOrDefault(errorCode.code(), errorCode.msg());
        return exception0(errorCode.code(), messagePattern, params);
    }

    /**
     * 创建指定编号的 HadokenServiceException 的异常
     *
     * @param code 编号
     * @return 异常
     */
    public static HadokenServiceException exception(Integer code) {
        return exception0(code, MESSAGES.get(code));
    }

    /**
     * 创建指定编号的 HadokenServiceException 的异常
     *
     * @param code   编号
     * @param params 消息提示的占位符对应的参数
     * @return 异常
     */
    public static HadokenServiceException exception(Integer code, Object... params) {
        return exception0(code, MESSAGES.get(code), params);
    }

    public static HadokenServiceException exception0(Integer code, String messagePattern, Object... params) {
        String message = doFormat(code, messagePattern, params);
        return new HadokenServiceException(code, message);
    }

    // ========== 格式化方法 ==========

    /**
     * 将错误编号对应的消息使用 params 进行格式化。
     *
     * @param code           错误编号
     * @param messagePattern 消息模版
     * @param params         参数
     * @return 格式化后的提示
     */
    @VisibleForTesting
    public static String doFormat(int code, String messagePattern, Object... params) {
        StringBuilder sbuf = new StringBuilder(messagePattern.length() + 50);
        int i = 0;
        int j;
        int l;
        for (l = 0; l < params.length; l++) {
            j = messagePattern.indexOf("{}", i);
            if (j == -1) {
                log.error("[doFormat][参数过多：错误码({})|错误内容({})|参数({})", code, messagePattern, params);
                if (i == 0) {
                    return messagePattern;
                } else {
                    sbuf.append(messagePattern.substring(i));
                    return sbuf.toString();
                }
            } else {
                sbuf.append(messagePattern, i, j);
                sbuf.append(params[l]);
                i = j + 2;
            }
        }
        if (messagePattern.indexOf("{}", i) != -1) {
            log.error("[doFormat][参数过少：错误码({})|错误内容({})|参数({})", code, messagePattern, params);
        }
        sbuf.append(messagePattern.substring(i));
        return sbuf.toString();
    }

}
