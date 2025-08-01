package com.hadoken.common.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hadoken.common.exception.ErrorCode;
import com.hadoken.common.exception.HadokenServiceException;
import com.hadoken.common.exception.enums.GlobalErrorCodeConstants;
import lombok.Data;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Objects;

/**
 * 通用返回对象
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/02/28 15:21
 */
@Data
public class CommonResult<T> implements Serializable {

    /**
     * 错误码
     *
     * @see ErrorCode#getCode()
     */
    private Integer code;

    /**
     * 返回数据对象
     */
    private T data;

    /**
     * 错误提示信息
     *
     * @see ErrorCode#getMsg()
     */
    private String msg;

    public static <T> CommonResult<T> error(CommonResult<?> result) {
        return error(result.getCode(), result.getMsg());
    }

    public static <T> CommonResult<T> error(Integer code, String message) {
        Assert.isTrue(!GlobalErrorCodeConstants.SUCCESS.getCode().equals(code), "code 必须是错误的！");
        CommonResult<T> result = new CommonResult<>();
        result.code = code;
        result.msg = message;
        return result;
    }

    public static <T> CommonResult<T> error(ErrorCode errorCode) {
        return error(errorCode.getCode(), errorCode.getMsg());
    }

    public static <T> CommonResult<T> success(T data) {
        CommonResult<T> result = new CommonResult<>();
        result.code = GlobalErrorCodeConstants.SUCCESS.getCode();
        result.data = data;
        result.msg = "";
        return result;
    }

    public static boolean isSuccess(Integer code) {
        return Objects.equals(code, GlobalErrorCodeConstants.SUCCESS.getCode());
    }

    // 避免 jackson 序列化
    @JsonIgnore
    public boolean isSuccess() {
        return isSuccess(code);
    }

    // 避免 jackson 序列化
    @JsonIgnore
    public boolean isError() {
        return !isSuccess();
    }


    // ========= 和 Exception 异常体系集成 =========

    /**
     * 判断是否有异常。如果有，则抛出 {@link HadokenServiceException} 异常
     */
    public void checkError() throws HadokenServiceException {
        if (isSuccess()) {
            return;
        }
        // 业务异常
        throw new HadokenServiceException(code, msg);
    }

    public static <T> CommonResult<T> error(HadokenServiceException serviceException) {
        return error(serviceException.getCode(), serviceException.getMessage());
    }
}
