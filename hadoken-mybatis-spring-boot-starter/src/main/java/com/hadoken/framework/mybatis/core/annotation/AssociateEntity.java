package com.hadoken.framework.mybatis.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 关联实体,用在类上
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/12/2 17:42
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AssociateEntity {

    Class<?> value();
}
