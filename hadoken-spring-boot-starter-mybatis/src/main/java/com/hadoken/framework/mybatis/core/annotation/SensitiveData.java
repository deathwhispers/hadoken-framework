package com.hadoken.framework.mybatis.core.annotation;

import java.lang.annotation.*;

/**
 * 定义在实体对象上, 通过该注解判断是否对该对象中的字段进行加解密
 * 配合
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/8/25 10:07
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SensitiveData {
}
