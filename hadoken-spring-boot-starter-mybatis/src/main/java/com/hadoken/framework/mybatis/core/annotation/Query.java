package com.hadoken.framework.mybatis.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yanggj
 * @version 1.0.0
 * @date 2022/5/11 9:12
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

    OrderBy orderBy() default OrderBy.EMPTY;

    /**
     * 查询类型
     */
    enum Type {
        // 相等
        EQUAL
        // 不等于
        , NOT_EQUAL
        // 大于
        , GREATER_THAN
        // 大于等于
        , GREATER_THAN_NQ
        // 小于
        , LESS_THAN
        // 小于等于
        , LESS_THAN_NQ
        // between
        , BETWEEN
        // not between
        , NOT_BETWEEN
        // 中模糊查询
        , INNER_LIKE
        // 中模糊查询
        , NOT_LIKE
        // 左匹配模糊查询
        , LEFT_LIKE
        // 右匹配模糊查询
        , RIGHT_LIKE
        //包含
        , IN
        // 不包含
        , NOT_IN
        // 为空
        , IS_NULL
        // 不为空
        , NOT_NULL
        // 为空白（包含null和空字符串）
        , IS_BLANK
        // 不为空白
        , NOT_BLANK

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

    enum OrderBy {
        ASC, DESC, EMPTY
    }

}
