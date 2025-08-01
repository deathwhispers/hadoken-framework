package com.hadoken.framework.mybatis.core.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hadoken.framework.mybatis.core.annotation.AssociateEntity;
import com.hadoken.framework.mybatis.core.annotation.Query;
import com.hadoken.framework.mybatis.core.condition.query.QueryWrapperX;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 查询工具类
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/5/23 15:40
 */
@Slf4j
@SuppressWarnings("all")
public class QueryHelper {

    public static <E, Q> QueryWrapper<E> getQueryWrapper(Q query) {
        QueryWrapperX<E> queryWrapper = new QueryWrapperX<>();
        if (query == null) {
            return queryWrapper;
        }
        try {
            AssociateEntity associateEntity = query.getClass().getAnnotation(AssociateEntity.class);
            List<String> entityFields = new ArrayList<>();
            if (associateEntity != null) {
                List<Field> allFields = getAllFields(associateEntity.value(), new ArrayList<>());
                entityFields = allFields.stream().map(entityField -> entityField.getName()).collect(Collectors.toList());
            }
            List<Field> fields = getAllFields(query.getClass(), new ArrayList<>());
            for (Field field : fields) {
                boolean accessible = field.isAccessible();
                // 设置对象的访问权限，保证对private的属性的访问
                field.setAccessible(true);
                Query q = field.getAnnotation(Query.class);
                if (q != null) {
                    // 数据库字段名
                    String propName = q.propName();
                    String blurry = q.blurry();
                    String attributeName = StrUtil.isBlank(propName) ? field.getName() : propName;
                    // 驼峰转为下划线
                    attributeName = StrUtil.toUnderlineCase(attributeName);

                    // 当排序字段不空时，增加排序(排序放到判空前，当排序字段为空时，仍能根据该字段排序)
                    queryWrapper.orderBy(q.orderBy() != Query.OrderBy.EMPTY, q.orderBy() == Query.OrderBy.ASC, attributeName);

                    Class<?> fieldType = field.getType();
                    Object val = field.get(query);
                    if (ObjectUtil.isNull(val) || "".equals(val)) {
                        continue;
                    }
                    // 模糊多字段
                    if (ObjectUtil.isNotEmpty(blurry)) {
                        String[] blurrys = blurry.split(StrUtil.COMMA);
                        if (ObjectUtil.isNotEmpty(val)) {
                            List<String> finalEntityFields = entityFields;
                            queryWrapper.and(wrapper -> {
                                for (String s : blurrys) {
                                    // todo 待优化
                                    if (CollectionUtil.isNotEmpty(finalEntityFields) && !finalEntityFields.contains(s)) {
                                        continue;
                                    }
                                    wrapper.like(StrUtil.toUnderlineCase(s), val).or();
                                }
                            });
                        }
                        continue;
                    }

                    // 实体类包含该字段时,才加入到 QueryWrapper 中
                    if (CollectionUtil.isNotEmpty(entityFields) && !entityFields.contains(attributeName)) {
                        continue;
                    }

                    // 单字段匹配
                    switch (q.type()) {
                        case EQUAL:
                            queryWrapper.eqIfPresent(attributeName, val);
                            break;
                        case NOT_EQUAL:
                            queryWrapper.neIfPresent(attributeName, val);
                            break;
                        case GREATER_THAN:
                            queryWrapper.gtIfPresent(attributeName, val);
                            break;
                        case GREATER_THAN_NQ:
                            queryWrapper.geIfPresent(attributeName, val);
                            break;
                        case LESS_THAN:
                            queryWrapper.ltIfPresent(attributeName, val);
                            break;
                        case LESS_THAN_NQ:
                            queryWrapper.leIfPresent(attributeName, val);
                            break;
                        case BETWEEN:
                            List<Object> between = new ArrayList<>((List<Object>) val);
                            if (between.size() == 2) {
                                queryWrapper.betweenIfPresent(attributeName, between.get(0), between.get(1));
                            }
                            break;
                        case NOT_BETWEEN:
                            List<Object> notBetween = new ArrayList<>((List<Object>) val);
                            if (notBetween.size() == 2) {
                                queryWrapper.notBetweenIfPresent(attributeName, notBetween.get(0), notBetween.get(1));
                            }
                            break;
                        case INNER_LIKE:
                            queryWrapper.likeIfPresent(attributeName, val);
                            break;
                        case NOT_LIKE:
                            queryWrapper.notLikeIfPresent(attributeName, val);
                            break;
                        case LEFT_LIKE:
                            // 注意：mybatisPlus中，likeleft是'%str'，likeRight是'str%'，与我们通常认识的左模糊查询和右模糊查询相左，此处做了转换
                            queryWrapper.likeRightIfPresent(attributeName, val);
                            break;
                        case RIGHT_LIKE:
                            queryWrapper.likeLeftIfPresent(attributeName, val);
                            break;
                        case IN:
                            Set<Object> in = new HashSet<>((Set<Object>) val);
                            queryWrapper.inIfPresent(attributeName, in);
                            break;
                        case NOT_IN:
                            Set<Object> notIn = new HashSet<>((Set<Object>) val);
                            queryWrapper.notInIfPresent(attributeName, notIn);
                            break;
                        case IS_NULL:
                            if (ObjectUtil.isNotEmpty(val)) {
                                if (((Boolean) val).equals(Boolean.TRUE)) {
                                    queryWrapper.isNull(attributeName);
                                } else {
                                    queryWrapper.isNotNull(attributeName);
                                }
                            }
                            break;
                        case NOT_NULL:
                            if (ObjectUtil.isNotEmpty(val)) {
                                if (((Boolean) val).equals(Boolean.TRUE)) {
                                    queryWrapper.isNotNull(attributeName);
                                } else {
                                    queryWrapper.isNull(attributeName);
                                }
                            }
                            break;
                        case IS_BLANK:
                            if (ObjectUtil.isNotEmpty(val)) {
                                if (((Boolean) val).equals(Boolean.TRUE)) {
                                    String finalAttributeName = attributeName;
                                    queryWrapper.and(wrapper -> {
                                        wrapper.isNull(finalAttributeName)
                                                .or()
                                                .eq(finalAttributeName, StrUtil.EMPTY);
                                    });
                                } else {
                                    queryWrapper.isNotNull(attributeName)
                                            .ne(attributeName, StrUtil.EMPTY);
                                }
                            }
                            break;
                        case NOT_BLANK:
                            if (ObjectUtil.isNotEmpty(val)) {
                                if (((Boolean) val).equals(Boolean.TRUE)) {
                                    queryWrapper.isNotNull(attributeName)
                                            .ne(attributeName, StrUtil.EMPTY);
                                } else {
                                    String finalAttributeName = attributeName;
                                    queryWrapper.and(wrapper -> {
                                        wrapper.isNull(finalAttributeName)
                                                .or()
                                                .eq(finalAttributeName, StrUtil.EMPTY);
                                    });
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
                field.setAccessible(accessible);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return queryWrapper;
    }


    public static List<Field> getAllFields(Class clazz, List<Field> fields) {
        if (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            getAllFields(clazz.getSuperclass(), fields);
        }
        return fields;
    }
}
