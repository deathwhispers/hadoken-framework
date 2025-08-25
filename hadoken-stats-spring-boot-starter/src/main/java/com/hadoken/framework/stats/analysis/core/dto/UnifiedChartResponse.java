package com.hadoken.framework.stats.analysis.core.dto;

import lombok.Data;

import java.util.List;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/22 23:19
 */
@Data
public class UnifiedChartResponse {
    /**
     * X轴或主分类轴的数据
     */
    private List<String> categories;

    /**
     * Y轴或系列数据
     */
    private List<ChartSeries> series;

    public UnifiedChartResponse(List<String> categories, List<ChartSeries> series) {
        this.categories = categories;
        this.series = series;
    }
}
