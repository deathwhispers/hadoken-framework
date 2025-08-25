package com.hadoken.framework.stats.period.util;

import com.hadoken.framework.stats.period.model.TimeRange;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * <b>统计周期工具类</b>：用于获取一段时间中的时间列表
 * <p>
 * 如：统计本月每一天的xxx，起始时间为 2025-08-01，结束时间为 2025-08-31，生成一个时间列表[2025-08-01,...,2025-08-31]
 * <ol>
 *   <li>支持按时间单位生成；如：按秒、分、时、天、周、月、年</li>
 *   <li>支持自定义步长；如：每 5 分钟、每 10分钟</li>
 * </ol>
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/08/11 11:39
 */
public class PeriodDates {

    private PeriodDates() {
        throw new AssertionError("No instance for utility class");
    }

    /**
     * 在指定时间范围内，按给定的时间单位和步长生成 LocalDateTime 列表
     *
     * @param startInclusive 起始时间（包含）
     * @param endExclusive   结束时间（不包含）
     * @param unit           时间单位（如：SECONDS, MINUTES, HOURS, DAYS）
     * @param step           步长（如：1 表示每1秒，5 表示每5分钟）
     * @return 按时间正序排列的 LocalDateTime 列表
     */
    public static List<LocalDateTime> between(
            LocalDateTime startInclusive,
            LocalDateTime endExclusive,
            ChronoUnit unit,
            long step) {

        if (step <= 0) {
            throw new IllegalArgumentException("Step must be positive");
        }
        if (startInclusive.isAfter(endExclusive)) {
            return Collections.emptyList();
        }

        // 特殊优化：按天处理（避免 LocalDateTime.plus(DAYS) 的时区问题）
        if (unit == ChronoUnit.DAYS) {
            return betweenDays(startInclusive, endExclusive, step);
        }

        // 通用处理：使用 Stream.iterate
        return Stream.iterate(startInclusive,
                        dateTime -> dateTime.isBefore(endExclusive),
                        dateTime -> dateTime.plus(step, unit))
                .collect(Collectors.toList());
    }

    /**
     * 优化版：按天生成（避免跨夏令时等问题）
     */
    private static List<LocalDateTime> betweenDays(
            LocalDateTime startInclusive,
            LocalDateTime endExclusive,
            long step) {

        ZoneId systemZone = ZoneId.systemDefault();

        LocalDate start = startInclusive.atZone(systemZone).toLocalDate();
        LocalDate end = endExclusive.atZone(systemZone).toLocalDate();

        return Stream.iterate(start,
                        date -> !date.isAfter(end),
                        date -> date.plusDays(step))
                .map(date -> date.atStartOfDay(systemZone).toLocalDateTime())
                .filter(dt -> !dt.isBefore(startInclusive) && dt.isBefore(endExclusive))
                .collect(Collectors.toList());
    }

    /**
     * 按秒生成（步长 = 1）
     */
    public static List<LocalDateTime> everySecond(
            LocalDateTime start, LocalDateTime end) {
        return between(start, end, ChronoUnit.SECONDS, 1);
    }

    /**
     * 按分钟生成（步长 = 1）
     */
    public static List<LocalDateTime> everyMinute(
            LocalDateTime start, LocalDateTime end) {
        return between(start, end, ChronoUnit.MINUTES, 1);
    }

    /**
     * 每小时生成
     */
    public static List<LocalDateTime> everyHour(
            LocalDateTime start, LocalDateTime end) {
        return between(start, end, ChronoUnit.HOURS, 1);
    }

    /**
     * 每天生成（00:00）
     */
    public static List<LocalDateTime> everyDay(
            LocalDateTime start, LocalDateTime end) {
        return between(start, end, ChronoUnit.DAYS, 1);
    }

    /**
     * 每 N 天生成
     */
    public static List<LocalDateTime> everyNDays(
            LocalDateTime start, LocalDateTime end, int days) {
        return between(start, end, ChronoUnit.DAYS, days);
    }

    /**
     * 将 LocalDateTime 列表转换为 LocalDate 列表（去时间）
     */
    public static List<LocalDate> toLocalDates(List<LocalDateTime> dateTimes) {
        return dateTimes.stream()
                .map(LocalDateTime::toLocalDate)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * 生成 LocalDate 列表（基于 ChronoUnit.DAYS）
     * 注意：只保留日期部分，时间归零
     *
     * @param step 步长，每 step 天
     */
    public static List<LocalDate> betweenDates(
            LocalDateTime start, LocalDateTime end,
            long step) {
        return toLocalDates(between(start, end, ChronoUnit.DAYS, step));
    }

    /**
     * 从 TimeRange 生成时间点列表
     */
    public static List<LocalDateTime> from(
            TimeRange range,
            ChronoUnit unit,
            long step) {
        return between(
                range.getStart(),
                range.getEnd(),
                unit,
                step
        );
    }
}
