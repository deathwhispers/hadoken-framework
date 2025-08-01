package com.hadoken.framework.mybatis.core.codec;

/**
 * 解密接口
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/8/25 10:14
 */
public interface Decrypt {

    /**
     * 解密
     *
     * @param result resultType的实例
     * @return T
     * @throws IllegalAccessException 字段不可访问异常
     */
    <T> T decrypt(T result) throws IllegalAccessException;
}
