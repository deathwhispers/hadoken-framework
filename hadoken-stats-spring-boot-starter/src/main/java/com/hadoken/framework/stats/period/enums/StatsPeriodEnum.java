package com.hadoken.framework.stats.period.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;


/**
 * 统计周期枚举通用类（包括常用定义，如：今日、昨日、本周、上周、本月、上月、本年、去年、近 7 天、近 30 天等等统计类型），后端将会统一转为起止时间，便于 sql 查询
 * 如：上月，前端传参 lastMonth（或 lastmonth、LASTMONTH... 大小写不敏感）
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/11 10:06
 */
@Schema(description = "统计周期枚举")
@Getter
@AllArgsConstructor
public enum StatsPeriodEnum {

    TODAY("today", "今日"),
    YESTERDAY("yesterday", "昨日"),
    THIS_WEEK("thisWeek", "本周"),
    LAST_WEEK("lastWeek", "上周"),
    THIS_MONTH("thisMonth", "本月"),
    LAST_MONTH("lastMonth", "上月"),
    THIS_QUARTER("thisQuarter", "本季度"),
    LAST_QUARTER("lastQuarter", "上季度"),
    THIS_YEAR("thisYear", "本年"),
    LAST_YEAR("lastYear", "去年");

    private final String code;
    private final String desc;

    /**
     * 静态查找方法（忽略大小写）
     */
    public static StatsPeriodEnum of(String code) {
        return Arrays.stream(values())
                .filter(p -> p.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的统计周期: " + code));
    }

    /**
     * 检查是否支持该 code
     */
    public static boolean contains(String code) {
        return Arrays.stream(values()).anyMatch(p -> p.code.equalsIgnoreCase(code));
    }
}

