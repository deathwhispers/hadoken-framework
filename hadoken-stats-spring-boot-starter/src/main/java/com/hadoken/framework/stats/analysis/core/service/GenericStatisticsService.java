package com.hadoken.framework.stats.analysis.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hadoken.framework.stats.analysis.core.mapper.GenericMetricMapper;
import com.hadoken.framework.stats.analysis.core.transform.ChartDataTransformer;
import com.hadoken.framework.stats.analysis.core.dto.QueryRequest;
import com.hadoken.framework.stats.analysis.core.dto.UnifiedChartResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/22 23:22
 */
@Service
public class GenericStatisticsService {
    private final GenericMetricMapper genericMapper;
    private final ChartDataTransformer chartDataTransformer; // 数据转换器，稍后定义

    public GenericStatisticsService(GenericMetricMapper genericMapper, ChartDataTransformer chartDataTransformer) {
        this.genericMapper = genericMapper;
        this.chartDataTransformer = chartDataTransformer;
    }

    public UnifiedChartResponse query(QueryRequest request) {
        // 1. 根据 domain 和 granularity 动态确定表名
        String tableName = "t_tcas_stats_" + request.getDomain() + "_" + request.getGranularity();

        // 2. 构建MyBatis-Plus的QueryWrapper
        QueryWrapper<Map<String, Object>> wrapper = new QueryWrapper<>();

        // 3. 构建 SELECT 子句 (核心)
        List<String> selectExpressions = new ArrayList<>();
        // a. 添加分组维度
        if (request.getGroupBy() != null) {
            request.getGroupBy().forEach(dim ->
                    selectExpressions.add("JSON_UNQUOTE(dimensions->'$." + dim + "') AS " + dim)
            );
        }
        // b. 添加聚合度量
        request.getMetrics().forEach(metric ->
                selectExpressions.add("SUM(CASE WHEN metric_name = '" + metric + "' THEN metric_value ELSE 0 END) AS " + metric)
        );
        wrapper.select(selectExpressions);

        // 4. 构建 WHERE 子句
        String timeColumn = "daily".equalsIgnoreCase(request.getGranularity()) ? "stat_date" : "stat_time";
        wrapper.between(timeColumn, request.getStartDate(), request.getEndDate());

        if (request.getFilters() != null) {
            request.getFilters().forEach((key, value) ->
                    wrapper.eq("JSON_UNQUOTE(dimensions->'$." + key + "')", value)
            );
        }

        // 5. 构建 GROUP BY 子句
        if (request.getGroupBy() != null && !request.getGroupBy().isEmpty()) {
            request.getGroupBy().forEach(dim -> wrapper.groupBy("JSON_UNQUOTE(dimensions->'$." + dim + "')"));
        }

        // 6. 执行查询，得到扁平化的结果
        List<Map<String, Object>> flatResults = genericMapper.query(tableName, wrapper);

        // 7. 调用转换器，将扁平结果转换为前端需要的图表格式
        return chartDataTransformer.transform(flatResults, request.getGroupBy());
    }

}
