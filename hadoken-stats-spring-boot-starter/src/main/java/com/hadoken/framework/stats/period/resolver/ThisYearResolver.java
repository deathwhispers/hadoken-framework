package com.hadoken.framework.stats.period.resolver;

import com.hadoken.framework.stats.period.model.StatsQueryDto;
import com.hadoken.framework.stats.period.model.TimeRange;

import java.time.*;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/11 11:08
 */
public class ThisYearResolver implements PeriodResolver {
    @Override
    public <T extends StatsQueryDto> TimeRange resolve(T request, ZoneId zoneId) {
        Year year = Year.now(zoneId);
        LocalDate end = year.atDay(year.length());
        return new TimeRange(
                year.atDay(1).atStartOfDay(),
                LocalDateTime.of(end, LocalTime.MAX),
                "本年"
        );
    }
}

