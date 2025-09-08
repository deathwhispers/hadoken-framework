package com.hadoken.common.exception;

import com.hadoken.common.exception.enums.GlobalErrorCodeConstants;
import com.hadoken.common.exception.enums.ServiceErrorCodeRange;
import lombok.Data;

/**
 * 错误码对象
 * 全局错误码，占用 [0, 999], 参见 {@link GlobalErrorCodeConstants}
 * 业务异常错误码，占用 [1 000 000 000, +∞)，参见 {@link ServiceErrorCodeRange}
 *
 * @param code 错误码
 * @param msg  错误提示
 * @author yanggj
 * @version 1.0.0
 * @date 2022/02/28 15:28
 */
public record ErrorCode(Integer code, String msg) {
}
