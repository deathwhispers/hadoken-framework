package com.hadoken.common.exception;

import com.hadoken.common.exception.enums.ServiceErrorCodeRange;

/**
 * hadoken 业务异常
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/02/28 15:33
 */
public final class HadokenServiceException extends RuntimeException {

    /**
     * 业务错误码
     *
     * @see ServiceErrorCodeRange
     */
    private Integer code;
    /**
     * 错误提示
     */
    private String message;

    /**
     * 空构造方法，避免反序列化问题
     */
    public HadokenServiceException() {
    }

    public HadokenServiceException(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMsg();
    }

    public HadokenServiceException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }


    public Integer getCode() {
        return code;
    }

    public HadokenServiceException setCode(Integer code) {
        this.code = code;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public HadokenServiceException setMessage(String message) {
        this.message = message;
        return this;
    }

}
