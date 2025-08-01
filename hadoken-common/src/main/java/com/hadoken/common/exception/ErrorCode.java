package com.hadoken.common.exception;

import lombok.Data;
import com.hadoken.common.exception.enums.GlobalErrorCodeConstants;
import com.hadoken.common.exception.enums.ServiceErrorCodeRange;

/**
 * 错误码对象
 * 全局错误码，占用 [0, 999], 参见 {@link GlobalErrorCodeConstants}
 * 业务异常错误码，占用 [1 000 000 000, +∞)，参见 {@link ServiceErrorCodeRange}
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/02/28 15:28
 */
@Data
public class ErrorCode {
    /**
     * 错误码
     */
    private final Integer code;
    /**
     * 错误提示
     */
    private final String msg;

    public ErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
