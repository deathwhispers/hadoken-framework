package com.github.hadoken.framework.mybatis.core.codec;

import com.github.hadoken.framework.mybatis.core.annotation.EncryptTransaction;
import com.github.hadoken.framework.mybatis.core.util.DBAESUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * 默认加密器实现
 *
 * @author yanggj
 * @version 2.0.0
 * @date 2022/8/25 10:16
 */
@Component
@Slf4j
public class DefaultEncryptor implements Encrypt {

    @Autowired
    private DBAESUtil dbAESUtil;

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
                        field.set(paramsObject, dbAESUtil.encrypt(value));
                    } catch (Exception e) {
                        log.error("字段加密失败, field: {}, value: {}, error: {}",
                            field.getName(), maskSensitiveData(value), e.getMessage(), e);
                        // 加密失败时保持原值，避免业务中断
                    }
                }
            }
        }
        return paramsObject;
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
