package com.hadoken.framework.stats.period.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 占比统计项
 *
 * @author yanggj
 * @version 1.0.0·
 * Created on 2025/7/24 11:07
 */
@Data
@Schema(description = "占比统计中的一个数据项")
public class RatioItem {

    @Schema(description = "分类名称，如：客车、货车", example = "客车")
    private String name;

    @Schema(description = "分类编号，如：130100", example = "130100")
    private String code;

    @Schema(description = "该分类的数量", example = "120")
    private Integer count;

    @Schema(description = "该分类的百分比占比（保留两位小数）", example = "52.17")
    private String ratio;

    @Schema(description = "前端展示颜色（可选）", example = "#FF5733")
    private String color;
}
