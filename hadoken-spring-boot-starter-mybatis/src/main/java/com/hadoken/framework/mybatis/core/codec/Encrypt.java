package com.hadoken.framework.mybatis.core.codec;

import java.lang.reflect.Field;

/**
 * 机密接口
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/8/25 10:15
 */
public interface Encrypt {

    /**
     * 加密
     *
     * @param declaredFields 加密字段
     * @param paramsObject   对象
     * @param <T>            入参类型
     * @return 返回加密
     * @throws IllegalAccessException 不可访问
     */
    <T> T encrypt(Field[] declaredFields, T paramsObject) throws IllegalAccessException;
}
