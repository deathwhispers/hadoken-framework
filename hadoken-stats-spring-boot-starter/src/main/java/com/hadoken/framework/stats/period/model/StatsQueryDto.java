package com.hadoken.framework.stats.period.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.DayOfWeek;

/**
 * 统计类型
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/11 10:52
 */
@Data
public class StatsQueryDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 周期类型，必填
     * 支持：
     * <ol>
     *  <li>today：今日</li>
     *  <li>thisWeek：本周</li>
     *  <li>thisMonth：本月</li>
     *  <li>thisQuarter：本季</li>
     *  <li>thisYear：本年</li>
     *  <li>yesterday：昨日</li>
     *  <li>lastWeek：上周</li>
     *  <li>lastMonth：上月</li>
     *  <li>lastQuarter：上季</li>
     *  <li>lastYear：去年</li>
     *  <li>last7Days：近 7 日</li>
     *  <li>last30Days：近 30 日</li>
     *  <li>days：需配合 {@link #lastN}  参数使用，表示近 {@link #lastN} 日</li>
     *  <li>weeks：需配合 {@link #lastN}  参数使用，近 {@link #lastN} 周</li>
     *  <li>months：需配合 {@link #lastN}  参数使用，近 {@link #lastN} 月</li>
     *  <li>years：需配合 {@link #lastN}  参数使用，近 {@link #lastN} 年</li>
     * </ol>
     */
    private String type;

    /**
     * 近 N 天/月/年...
     */
    private Integer lastN;

    /**
     * 用于 quarter、week 等需要额外参数的类型
     * 如：quarter → "2025-Q2"，week → "2025-W12"
     */
    private String value;

    /**
     * 指定周起始日，可选，默认为 null（使用系统默认或配置）
     * 如：MONDAY, SUNDAY
     */
    private String weekStart;

    // -------------------- 便捷方法（可选） --------------------

    public DayOfWeek parseDayOfWeek() {
        if (weekStart == null || weekStart.isEmpty()) {
            return null;
        }
        try {
            return DayOfWeek.valueOf(weekStart.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无效的周起始日: " + weekStart);
        }
    }

    public boolean hasN() {
        return lastN != null && lastN > 0;
    }

    public boolean hasValue() {
        return value != null && !value.trim().isEmpty();
    }
}
