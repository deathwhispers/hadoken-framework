package com.hadoken.framework.stats.period.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 同比统计分析返回对象
 * 和去年同期相比较
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/11 09:39
 */
@Data
@Schema(description = "同比统计分析返回对象（Year-on-Year）")
public class YoYStatsVo {

    @Schema(description = "当前统计周期的数据", implementation = TimeSeriesStatsVo.class)
    private TimeSeriesStatsVo current;

    @Schema(description = "去年同期周期的数据", implementation = TimeSeriesStatsVo.class)
    private TimeSeriesStatsVo comparison;

    @Schema(description = "同比变化率，单位为百分比（如：15.5 表示 +15.5%, -13.21 表示 -13.21%）", example = "12.3")
    private Double growthRate;

    @Schema(description = "统计指标名称，如：车流量、在线设备数", example = "车流量")
    private String metricName;

    @Schema(description = "时间范围描述，如：2025-04 vs 2024-04", example = "2025-04 vs 2024-04")
    private String periodLabel;

    @Schema(description = "额外信息，如最大值差、趋势一致性等，json 格式", example = "{\"max_diff\": 20, \"trend_aligned\": true}")
    private String extra;
}
