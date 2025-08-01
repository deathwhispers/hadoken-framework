package com.hadoken.common.util.number;

import com.hadoken.common.util.string.StringUtils;

/**
 * 数字的工具类，补全 {@link cn.hutool.core.util.NumberUtil} 的功能
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/02/28 16:07
 */
public class NumberUtils {

    public static Long parseLong(String str) {
        return StringUtils.isNotEmpty(str) ? Long.valueOf(str) : null;
    }

}