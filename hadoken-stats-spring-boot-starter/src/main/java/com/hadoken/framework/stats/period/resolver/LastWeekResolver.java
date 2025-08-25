package com.hadoken.framework.stats.period.resolver;

import com.hadoken.framework.stats.period.model.StatsQueryDto;
import com.hadoken.framework.stats.period.model.TimeRange;

import java.time.*;
import java.time.temporal.TemporalAdjusters;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/11 11:08
 */
public class LastWeekResolver implements PeriodResolver {
    @Override
    public <T extends StatsQueryDto> TimeRange resolve(T request, ZoneId zoneId) {
        LocalDate now = LocalDate.now(zoneId);
        DayOfWeek firstDayOfWeek = request.parseDayOfWeek();
        if (firstDayOfWeek == null) {
            firstDayOfWeek = DayOfWeek.MONDAY;
        }
        LocalDate start = now.minusWeeks(1).with(TemporalAdjusters.previousOrSame(firstDayOfWeek));
        LocalDate end = start.plusDays(6);
        return new TimeRange(
                start.atStartOfDay(),
                LocalDateTime.of(end, LocalTime.MAX),
                "上周"
        );
    }
}

