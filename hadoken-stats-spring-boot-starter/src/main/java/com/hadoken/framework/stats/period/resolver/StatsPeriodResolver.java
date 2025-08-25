package com.hadoken.framework.stats.period.resolver;


import com.hadoken.framework.stats.period.model.StatsQueryDto;
import com.hadoken.framework.stats.period.model.TimeRange;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/11 10:20
 */
public class StatsPeriodResolver {

    private final Map<String, PeriodResolver> resolverMap;
    private final ZoneId zoneId;
    private Map<String, String> lowercaseToOriginalKey;

    public StatsPeriodResolver(Map<String, PeriodResolver> resolverMap, ZoneId zoneId) {
        this.resolverMap = resolverMap;
        this.zoneId = zoneId;
        buildCaseInsensitiveIndex();
    }

    private void buildCaseInsensitiveIndex() {
        lowercaseToOriginalKey = new HashMap<>();
        resolverMap.forEach((key, value) -> {
            lowercaseToOriginalKey.put(key.toUpperCase(), key);
        });
    }

    public TimeRange resolve(StatsQueryDto request) {
        String type = request.getType();
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("统计周期类型 type 不能为空");
        }
        String normalized = type.trim().toUpperCase();
        String originalKey = lowercaseToOriginalKey.get(normalized);
        PeriodResolver resolver = resolverMap.get(originalKey);
        if (resolver == null) {
            String supported = String.join(", ", resolverMap.keySet());
            throw new IllegalArgumentException(
                    "不支持的统计周期类型: '" + type + "'. 支持的类型: [" + supported + "]");
        }
        return resolver.resolve(request, zoneId);
    }

    // ================== 本季/上季度 ==================

    public TimeRange thisQuarter() {
        LocalDate now = LocalDate.now(zoneId);
        // 获取当前季度的第一月
        Month firstMonthOfQuarter = now.getMonth().firstMonthOfQuarter();
        LocalDate start = YearMonth.from(now).withMonth(firstMonthOfQuarter.getValue()).atDay(1);
        LocalDate end = start.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
        return new TimeRange(
                start.atStartOfDay(),
                LocalDateTime.of(end, LocalTime.MAX),
                "本季度"
        );
    }

    public TimeRange lastQuarter() {
        LocalDate now = LocalDate.now(zoneId);
        // 上个季度 = 当前时间减3个月，然后取该月所在季度的第一天
        LocalDate threeMonthsAgo = now.minusMonths(3);
        int quarter = (threeMonthsAgo.getMonthValue() - 1) / 3 + 1;
        int firstMonth = (quarter - 1) * 3 + 1;
        LocalDate start = LocalDate.of(threeMonthsAgo.getYear(), firstMonth, 1);
        LocalDate end = start.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());

        return new TimeRange(
                start.atStartOfDay(),
                LocalDateTime.of(end, LocalTime.MAX),
                "上季度"
        );
    }

    // ================== 近 N 天 ==================

    public TimeRange lastNDays(int n) {
        LocalDateTime end = LocalDateTime.now(zoneId);
        LocalDateTime begin = end.minusDays(n);
        return new TimeRange(
                begin,
                end,
                "近" + n + "天"
        );
    }

    // ================== 指定年季度：如 2025-Q2 ==================

    public TimeRange ofQuarter(String quarterStr) {
        // 格式：YYYY-Q1, YYYY-Q2, YYYY-Q3, YYYY-Q4
        if (!quarterStr.toUpperCase().matches("\\d{4}-Q[1-4]")) {
            throw new IllegalArgumentException("季度格式错误，应为 YYYY-Q1~Q4，如 2025-Q2");
        }
        String[] parts = quarterStr.toUpperCase().split("-Q");
        int year = Integer.parseInt(parts[0]);
        int quarter = Integer.parseInt(parts[1]);

        Year y = Year.of(year);
        Month firstMonth = Month.of((quarter - 1) * 3 + 1); // Q1:1, Q2:4, Q3:7, Q4:10
        YearMonth startMonth = y.atMonth(firstMonth);

        LocalDate start = startMonth.atDay(1);
        LocalDate end = start.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());

        return new TimeRange(
                start.atStartOfDay(),
                LocalDateTime.of(end, LocalTime.MAX),
                quarterStr.toUpperCase()
        );
    }

    // ================== 指定某周：如 2025-W12 ==================

    public TimeRange ofWeek(String weekStr, DayOfWeek firstDayOfWeek) {
        // 格式：YYYY-Www
        if (!weekStr.toUpperCase().matches("\\d{4}-W\\d{1,2}")) {
            throw new IllegalArgumentException("周格式错误，应为 YYYY-Www，如 2025-W12");
        }
        String[] parts = weekStr.split("-W");
        int year = Integer.parseInt(parts[0]);
        int weekOfYear = Integer.parseInt(parts[1]);

        LocalDate firstDayOfYear = Year.of(year).atDay(1);
        // 找到该年第 weekOfYear 周的第一天
        LocalDate start = firstDayOfYear
                .with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
                .plusWeeks(weekOfYear - 1);
        LocalDate end = start.plusDays(6);

        return new TimeRange(
                start.atStartOfDay(),
                LocalDateTime.of(end, LocalTime.MAX),
                weekStr.toUpperCase()
        );
    }
}