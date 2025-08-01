package com.hadoken.framework.mybatis.core.codec;

import org.springframework.stereotype.Component;
import com.hadoken.framework.mybatis.core.annotation.EncryptTransaction;
import com.hadoken.framework.mybatis.core.util.DBAESUtil;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * 默认解密
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/8/25 10:18
 */
@Component
public class DefaultDecryptor implements Decrypt {

    @Override
    public <T> T decrypt(T result) throws IllegalAccessException {

        // 取出resultType的类
        Class<?> resultClass = result.getClass();
        Field[] declaredFields = resultClass.getDeclaredFields();
        for (Field field : declaredFields) {

            // 取出所有被DecryptTransaction注解的字段
            EncryptTransaction encryptTransaction = field.getAnnotation(EncryptTransaction.class);
            if (!Objects.isNull(encryptTransaction)) {
                field.setAccessible(true);
                Object object = field.get(result);

                // String的解密
                if (object instanceof String) {
                    String value = (String) object;

                    // 对注解的字段进行逐一解密
                    try {
                        field.set(result, DBAESUtil.decrypt(value));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }
}
