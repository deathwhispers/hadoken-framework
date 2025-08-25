package com.hadoken.framework.stats.period.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;


/**
 * 时间单位枚举类
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/11 09:27
 */
@Getter
@AllArgsConstructor
public enum TemporalUnitEnum {
    SECONDS("秒", "yyyy-MM-dd HH:mm:ss", ChronoUnit.SECONDS),
    MINUTES("分", "yyyy-MM-dd HH:mm", ChronoUnit.MINUTES),
    HOURS("小时", "yyyy-MM-dd HH", ChronoUnit.HOURS),
    DAYS("天", "yyyy-MM-dd", ChronoUnit.DAYS),
    WEEKS("周", "yyyy-MM-dd", ChronoUnit.WEEKS),
    MONTHS("月", "yyyy-MM", ChronoUnit.MONTHS),
    YEARS("年", "yyyy", ChronoUnit.YEARS);

    private final String desc;
    private final String pattern;
    private final ChronoUnit chronoUnit;

    /**
     * 获取对应的 ChronoUnit，QUARTER 特殊处理返回 null
     */
    public TemporalUnit getTemporalUnit() {
        return chronoUnit;
    }

    /**
     * 根据 name（如 "MONTHS"）查找枚举
     */
    public static TemporalUnitEnum of(String name) {
        for (TemporalUnitEnum unit : values()) {
            if (unit.name().equalsIgnoreCase(name)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("不支持的时间单位: " + name);
    }

    /**
     * 判断是否包含该时间单位
     */
    public static boolean contains(String name) {
        return Arrays.stream(values())
                .anyMatch(unit -> unit.name().equalsIgnoreCase(name));
    }

    /**
     * 快速获取 ChronoUnit，不支持则抛异常（用于需要 ChronoUnit 的场景）
     */
    public ChronoUnit requireChronoUnit() {
        if (chronoUnit == null) {
            throw new UnsupportedOperationException("ChronoUnit 不支持: " + this.name());
        }
        return chronoUnit;
    }
}
