package com.hadoken.framework.stats.period.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 时序统计项
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/7/24 11:14
 */
@Data
@Schema(description = "时序统计中的单个时间点数据项")
public class TimeSeriesItem {

    @Schema(description = "时间点，格式由业务决定，如：yyyy-MM-dd HH:mm:ss", example = "2025-04-01 08:00:00,2025-04,2025-08-08,20250724")
    private String timestamp;

    @Schema(description = "该时间点的统计值", example = "120")
    private Integer value;

    @Schema(description = "额外数值（可选），如持续时间、平均值等")
    private String extraValue;

    @Schema(description = "额外信息（可选），如备注、状态等", example = "{\"status\": \"high\"}")
    private String remark;

}
