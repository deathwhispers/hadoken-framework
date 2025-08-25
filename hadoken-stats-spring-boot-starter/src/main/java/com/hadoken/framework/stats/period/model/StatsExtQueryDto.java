package com.hadoken.framework.stats.period.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 统计类型
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/11 10:52
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class StatsExtQueryDto extends StatsQueryDto implements Serializable {

    @Schema(description = "集团 id")
    private String groupId;

    @Schema(description = "机构id")
    private String orgId;

    @Schema(description = "路段id")
    private String waySectionId;

    @Schema(description = "设施id")
    private String facilitiesId;

}
