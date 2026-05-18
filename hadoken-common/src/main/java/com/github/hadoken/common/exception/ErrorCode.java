package com.github.hadoken.common.exception;

import com.github.hadoken.common.enums.GlobalErrorCodeConstants;

/**
 * 错误码对象
 * 全局错误码，占用 [0, 999], 参见 {@link GlobalErrorCodeConstants}
 *
 * @param code 错误码
 * @param msg  错误提示
 * @author yanggj
 * @version 1.0.0
 * @date 2022/02/28 15:28
 */
public record ErrorCode(Integer code, String msg) {
}
