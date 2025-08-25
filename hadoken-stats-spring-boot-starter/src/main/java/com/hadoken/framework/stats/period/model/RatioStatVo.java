package com.hadoken.framework.stats.period.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 占比统计通用返回对象
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/7/24 11:09
 */
@Data
@Schema(description = "通用占比统计返回对象，适用于各种分类占比展示")
public class RatioStatVo {

    @Schema(description = "统计项列表")
    private List<RatioItem> data;

    @Schema(description = "统计单位，如：辆、人次、事件数", example = "辆")
    private String unit;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "统计类型标识，如：vehicle_type, incident_type", example = "vehicle_type")
    private String statsType;

    @Schema(description = "额外信息，如总数、最大占比项等, json 格式", example = "{\"total\":230, \"max\":\"客车\"}")
    private String extra;

}
