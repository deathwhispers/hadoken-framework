package com.hadoken.framework.stats.analysis.core.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/22 23:19
 */
@Data
public class ChartSeries {

    /**
     * 系列名称 (用于图例)
     */
    private String name;

    /**
     * 系列对应的具体数值
     */
    private List<BigDecimal> data;

    public ChartSeries(String name, List<BigDecimal> data) {
        this.name = name;
        this.data = data;
    }
}
