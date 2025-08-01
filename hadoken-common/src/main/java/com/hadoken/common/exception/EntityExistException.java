package com.hadoken.common.exception;

import org.springframework.util.StringUtils;

/**
 * @author yanggj
 * @date 2022/03/09 14:18
 */
public class EntityExistException extends RuntimeException {

    public EntityExistException(Class<?> clazz, String field, String val) {
        super(EntityExistException.generateMessage(clazz.getSimpleName(), field, val));
    }

    private static String generateMessage(String entity, String field, String val) {
        return StringUtils.capitalize(entity)
                + " with " + field + " " + val + " existed";
    }
}