package com.hadoken.framework.stats.period.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 时序统计通用返回对象
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/7/24 10:36
 */
@Data
@Schema(description = "时序统计分析基类")
public class TimeSeriesStatsVo {

    @Schema(description = "时间序列数据列表")
    private List<TimeSeriesItem> data;

    @Schema(description = "统计单位，如：人次、辆次、人数、设备数等", example = "辆次")
    private String unit;

    @Schema(description = "统计类型标识，如：car_flow, pedestrian_flow, online_count", example = "car_flow")
    private String statsType;

    @Schema(description = "统计时间段描述，如：hourly, daily, weekly", example = "hourly")
    private String interval;

    @Schema(description = "额外信息，如最大值、平均值等，json 格式", example = "{\"max\":150, \"avg\":135}")
    private String extra;

}
