package com.hadoken.framework.datasource.core.enums;

/**
 * 对应于多数据源中不同数据源配置
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/02/28 14:53
 */
public interface DataSourceEnum {

    /**
     * 主库，推荐使用 {@link com.baomidou.dynamic.datasource.annotation.Master} 注解
     */
    String MASTER = "master";
    /**
     * 从库，推荐使用 {@link com.baomidou.dynamic.datasource.annotation.Slave} 注解
     */
    String SLAVE = "slave";

}
