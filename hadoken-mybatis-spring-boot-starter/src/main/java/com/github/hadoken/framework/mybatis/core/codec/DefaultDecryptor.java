package com.github.hadoken.framework.mybatis.core.codec;

import com.github.hadoken.framework.mybatis.core.annotation.EncryptTransaction;
import com.github.hadoken.framework.mybatis.core.util.DBAESUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * 默认解密器实现
 *
 * @author yanggj
 * @version 2.0.0
 * @date 2022/8/25 10:18
 */
@Component
@Slf4j
public class DefaultDecryptor implements Decrypt {

    @Autowired
    private DBAESUtil dbAESUtil;

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
                        field.set(result, dbAESUtil.decrypt(value));
                    } catch (Exception e) {
                        log.error("字段解密失败, field: {}, value: {}, error: {}",
                            field.getName(), maskSensitiveData(value), e.getMessage(), e);
                        // 解密失败时保持原值，可能是未加密的数据
                    }
                }
            }
        }
        return result;
    }

    /**
     * 掩码敏感数据（用于日志）
     */
    private String maskSensitiveData(String data) {
        if (data == null || data.length() <= 8) {
            return "***";
        }
        // 显示前4位和后4位，中间用*代替
        return data.substring(0, 4) + "***" + data.substring(data.length() - 4);
    }
}
