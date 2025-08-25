package com.hadoken.framework.stats.analysis.core.transform;

import com.hadoken.framework.stats.analysis.core.dto.ChartSeries;
import com.hadoken.framework.stats.analysis.core.dto.UnifiedChartResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/22 23:24
 */
@Component
public class ChartDataTransformer {

    /**
     * 将扁平的SQL查询结果转换为层级的图表响应格式
     *
     * @param flatResults 扁平数据列表
     * @param groupBy     分组维度
     * @return 标准图表响应
     */
    public UnifiedChartResponse transform(List<Map<String, Object>> flatResults, List<String> groupBy) {
        if (flatResults == null || flatResults.isEmpty()) {
            return new UnifiedChartResponse(List.of(), List.of());
        }

        // 简化处理：目前只处理单维度和双维度分组
        if (groupBy == null || groupBy.isEmpty() || groupBy.size() > 2) {
            // 对于更复杂的情况或无分组的情况，可以返回原始数据或特定格式
            // 此处返回一个简单的系列
            String metricName = flatResults.get(0).keySet().stream().filter(k -> !groupBy.contains(k)).findFirst().orElse("value");
            List<BigDecimal> data = flatResults.stream()
                    .map(row -> (BigDecimal) row.get(metricName))
                    .collect(Collectors.toList());
            return new UnifiedChartResponse(List.of("总计"), List.of(new ChartSeries("总计", data)));
        }

        if (groupBy.size() == 1) {
            return transformSingleDimension(flatResults, groupBy.get(0));
        } else {
            return transformMultiDimension(flatResults, groupBy.get(0), groupBy.get(1));
        }
    }

    // 处理单维度分组 (饼图/柱状图)
    private UnifiedChartResponse transformSingleDimension(List<Map<String, Object>> flatResults, String dimension) {
        List<String> categories = new ArrayList<>();
        List<BigDecimal> data = new ArrayList<>();
        String metricName = flatResults.get(0).keySet().stream().filter(k -> !dimension.equals(k)).findFirst().orElse("value");

        for (Map<String, Object> row : flatResults) {
            categories.add(String.valueOf(row.get(dimension)));
            data.add((BigDecimal) row.get(metricName));
        }
        ChartSeries series = new ChartSeries(metricName, data);
        return new UnifiedChartResponse(categories, List.of(series));
    }

    // 处理双维度分组 (分组柱状图/多系列折线图)
    private UnifiedChartResponse transformMultiDimension(List<Map<String, Object>> flatResults, String categoryDim, String seriesDim) {
        // 使用 LinkedHashMap 保证顺序
        Map<String, Map<String, BigDecimal>> pivotMap = new LinkedHashMap<>();
        List<String> seriesNames = flatResults.stream().map(row -> String.valueOf(row.get(seriesDim))).distinct().sorted().toList();
        String metricName = flatResults.get(0).keySet().stream().filter(k -> !categoryDim.equals(k) && !seriesDim.equals(k)).findFirst().orElse("value");

        for (Map<String, Object> row : flatResults) {
            String category = String.valueOf(row.get(categoryDim));
            String seriesName = String.valueOf(row.get(seriesDim));
            BigDecimal value = (BigDecimal) row.get(metricName);

            pivotMap.computeIfAbsent(category, k -> new LinkedHashMap<>()).put(seriesName, value);
        }

        List<String> categories = new ArrayList<>(pivotMap.keySet());
        List<ChartSeries> seriesList = new ArrayList<>();

        for (String seriesName : seriesNames) {
            List<BigDecimal> data = new ArrayList<>();
            for (String category : categories) {
                data.add(pivotMap.get(category).getOrDefault(seriesName, BigDecimal.ZERO));
            }
            seriesList.add(new ChartSeries(seriesName, data));
        }

        return new UnifiedChartResponse(categories, seriesList);
    }
}
