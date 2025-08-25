package com.hadoken.framework.stats.analysis.core.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 通用指标Mapper，用于执行动态的数据库操作。
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/24 14:04
 */
@Mapper
public interface GenericMetricMapper {

    /**
     * 批量插入或更新（累加）指标数据。
     *
     * @param tableName 目标表名
     * @param list      要插入的数据列表
     */
    void batchUpsert(@Param("tableName") String tableName, @Param("list") List<Map<String, Object>> list);

    /**
     * 通用查询方法。
     *
     * @param tableName 目标表名
     * @param wrapper   MyBatis-Plus的QueryWrapper
     * @return 查询结果
     */
    List<Map<String, Object>> query(@Param("tableName") String tableName, @Param("ew") Wrapper<Map<String, Object>> wrapper);
}
