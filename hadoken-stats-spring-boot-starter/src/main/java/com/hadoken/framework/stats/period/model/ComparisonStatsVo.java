package com.hadoken.framework.stats.period.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 通用对比统计返回对象（支持同比、环比、自定义周期对比）
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/11 09:43
 */
@Data
@Schema(description = "通用对比统计返回对象（支持同比、环比、自定义周期对比）")
public class ComparisonStatsVo {


    @Schema(description = "基准周期数据", implementation = TimeSeriesStatsVo.class)
    private TimeSeriesStatsVo base;

    @Schema(description = "对比周期数据", implementation = TimeSeriesStatsVo.class)
    private TimeSeriesStatsVo compare;

    @Schema(description = "变化率，百分比值", example = "8.9")
    private Double growthRate;

    @Schema(description = "变化类型：increase, decrease, stable", example = "increase")
    private String trend;

    @Schema(description = "指标名称", example = "在线用户数")
    private String metricName;

    @Schema(description = "对比类型：yoy, mom, wow, custom", example = "yoy")
    private String comparisonType;

    @Schema(description = "周期描述", example = "2025-Q2 vs 2024-Q2")
    private String periodLabel;

    @Schema(description = "主统计值（可选），如当前周期总和或平均值", example = "1250")
    private Integer mainValue;

    @Schema(description = "对比统计值（可选）", example = "1120")
    private Integer compareValue;

    @Schema(description = "额外分析信息，json 格式", example = "{\"peak_shift\": \"yes\", \"anomaly_detected\": false}")
    private String extra;

}
