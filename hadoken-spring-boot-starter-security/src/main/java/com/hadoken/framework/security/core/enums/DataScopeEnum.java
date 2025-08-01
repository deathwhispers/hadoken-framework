package com.hadoken.framework.security.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据范围枚举类
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/03/01 18:25
 */
@Getter
@AllArgsConstructor
public enum DataScopeEnum {

    // 全部数据权限
    ALL(1, "全部数据权限"),

    // 指定部门数据权限
    DEPT_CUSTOM(2, "指定部门数据权限"),

    // 部门数据权限
    DEPT_ONLY(3, "部门数据权限"),

    // 部门及以下数据权限
    DEPT_AND_CHILD(4, "部门及以下数据权限"),

    // 仅本人数据权限
    SELF(5, "仅本人数据权限");

    /**
     * 范围
     */
    private final Integer scope;
    private final String desc;

    public static DataScopeEnum find(Integer val) {
        for (DataScopeEnum dataScopeEnum : DataScopeEnum.values()) {
            if (val.equals(dataScopeEnum.getScope())) {
                return dataScopeEnum;
            }
        }
        return null;
    }

}
