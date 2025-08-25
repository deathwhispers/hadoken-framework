package com.hadoken.framework.stats.analysis.core.dto;

import lombok.Data;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/24 17:06
 */
@Data
public class MetricSpec {
    private String name;
    private String aggType = "SUM"; // 默认为SUM
    private String alias; // 别名
    private String calcType; // 派生计算 (e.g., "YOY", "MOM")
}
