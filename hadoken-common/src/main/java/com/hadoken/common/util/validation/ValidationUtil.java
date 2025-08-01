package com.hadoken.common.util.validation;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.hadoken.common.exception.BadRequestException;
import com.hadoken.common.util.string.StringUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * 校验工具类
 *
 * @author yanggj
 */
public class ValidationUtil {

    private static final Pattern PATTERN_URL = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    private static final Pattern PATTERN_XML_NCNAME = Pattern.compile("[a-zA-Z_][\\-_.0-9_a-zA-Z$]*");

    public static boolean isMobile(String mobile) {
        if (StringUtils.length(mobile) != 11) {
            return false;
        }
        // TODO 后面完善手机校验
        return true;
    }

    /**
     * 验证空
     */
    public static void isNull(Object obj, String entity, String parameter, Object value) {
        if (ObjectUtil.isNull(obj)) {
            String msg = entity + " 不存在: " + parameter + " is " + value;
            throw new BadRequestException(msg);
        }
    }

    /**
     * 验证是否为邮箱
     */
    public static boolean isEmail(String email) {
        return true;
    }

    public static void validate(Validator validator, Object object, Class<?>... groups) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
        if (CollUtil.isNotEmpty(constraintViolations)) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }

}
