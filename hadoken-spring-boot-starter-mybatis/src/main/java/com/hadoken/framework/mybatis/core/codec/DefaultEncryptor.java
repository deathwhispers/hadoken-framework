package com.hadoken.framework.mybatis.core.codec;

import org.springframework.stereotype.Component;
import com.hadoken.framework.mybatis.core.annotation.EncryptTransaction;
import com.hadoken.framework.mybatis.core.util.DBAESUtil;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * 默认加密
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/8/25 10:16
 */
@Component
public class DefaultEncryptor implements Encrypt {

    @Override
    public <T> T encrypt(Field[] declaredFields, T paramsObject) throws IllegalAccessException {

        // 取出所有被EncryptTransaction注解的字段
        for (Field field : declaredFields) {
            EncryptTransaction encryptTransaction = field.getAnnotation(EncryptTransaction.class);
            if (!Objects.isNull(encryptTransaction)) {
                field.setAccessible(true);
                Object object = field.get(paramsObject);

                // 暂时只实现String类型的加密
                if (object instanceof String) {
                    String value = (String) object;

                    // 加密
                    try {
                        field.set(paramsObject, DBAESUtil.encrypt(value));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return paramsObject;
    }
}
