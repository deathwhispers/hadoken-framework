package com.hadoken.framework.stats.period.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/11 09:41
 */
@Data
@Schema(description = "环比统计分析返回对象（Period-on-Period），如月环比、周环比、日环比等")
public class PoPStatsVo {

    @Schema(description = "当前周期的数据", implementation = TimeSeriesStatsVo.class)
    private TimeSeriesStatsVo current;

    @Schema(description = "上一周期的数据（如上月、昨日）", implementation = TimeSeriesStatsVo.class)
    private TimeSeriesStatsVo previous;

    @Schema(description = "环比变化率，单位为百分比（如：-5.2 表示 -5.2%）", example = "-3.7")
    private Double growthRate;

    @Schema(description = "统计指标名称", example = "日活跃设备数")
    private String metricName;

    @Schema(description = "周期类型，如：daily, weekly, monthly", example = "monthly")
    private String interval;

    @Schema(description = "时间范围标签，如：2025-07 vs 2025-06", example = "2025-07 vs 2025-06")
    private String periodLabel;

    @Schema(description = "额外信息，如波动幅度、连续增长天数等，json 格式", example = "{\"volatility\": 8.1, \"consecutive_increase_days\": 3}")
    private String extra;

}
