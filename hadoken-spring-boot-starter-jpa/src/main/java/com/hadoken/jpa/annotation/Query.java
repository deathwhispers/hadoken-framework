package com.hadoken.jpa.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yanggj
 * @date 2022/03/09 16:04
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {

    /**
     * 对象的属性名
     */
    String propName() default "";

    /**
     * 查询方式，默认为equal
     */
    Type type() default Type.EQUAL;

    /**
     * 连接查询的属性名，如User类中的dept
     */
    String joinName() default "";

    /**
     * 连接查询，默认为左连接
     */
    Join join() default Join.LEFT;

    /**
     * 多字段模糊搜索，仅支持String类型字段，多个用逗号隔开, 如@Query(blurry = "email,username")
     */
    String blurry() default "";

    /**
     * 查询类型
     */
    enum Type {
        // 相等
        EQUAL

        // 大于
        , GREATER_THAN

        // 小于
        , LESS_THAN

        // 中模糊查询
        , INNER_LIKE

        // 左模糊查询
        , LEFT_LIKE

        // 右模糊查询
        , RIGHT_LIKE

        // 小于等于
        , LESS_THAN_NQ

        //包含
        , IN

        // 不包含
        , NOT_IN

        // 不等于
        , NOT_EQUAL

        // between
        , BETWEEN
        // 不为空

        , NOT_NULL

        // 为空
        , IS_NULL
    }

    /**
     * 适用于简单连接查询，复杂的请自定义该注解，或者使用sql查询
     */
    enum Join {
        // 左连接
        LEFT,

        // 右连接
        RIGHT,

        // 内连接（自然连接）
        INNER
    }


}
