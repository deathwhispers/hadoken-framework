package com.hadoken.framework.stats.analysis.core.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/22 23:18
 */
@Data
public class QueryRequest {
    /**
     * 业务领域 (e.g., "traffic")
     */
    private String domain;

    /**
     * 时间粒度 (e.g., "hourly")
     */
    private String granularity;

    /**
     * 需要查询的度量列表 (e.g., ["vehicle_flow"])
     */
    private List<String> metrics;

    /**
     * 分组维度列表 (e.g., ["direction", "vehicle_type"])
     */
    private List<String> groupBy;

    /**
     * 过滤条件 (e.g., {"facilities_type": "TOLL_STATION"})
     */
    private Map<String, Object> filters;

    /**
     * 开始日期 (e.g., "2025-08-19")
     */
    private String startDate;

    /**
     * 结束日期 (e.g., "2025-08-19")
     */
    private String endDate;
}
