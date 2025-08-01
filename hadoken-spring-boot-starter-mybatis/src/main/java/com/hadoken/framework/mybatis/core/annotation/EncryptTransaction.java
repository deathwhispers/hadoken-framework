package com.hadoken.framework.mybatis.core.annotation;

import java.lang.annotation.*;

/**
 * 可作用于字段和方法参数上
 * 1. 在实体类中使用
 * 2. 在 mapper 中的方法参数上使用
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/8/25 10:10
 */
@Inherited
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EncryptTransaction {
}
