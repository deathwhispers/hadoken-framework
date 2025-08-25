package com.hadoken.framework.stats.period.resolver;

import com.hadoken.framework.stats.period.model.StatsQueryDto;
import com.hadoken.framework.stats.period.model.TimeRange;

import java.time.*;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/11 11:08
 */
public class LastYearResolver implements PeriodResolver {
    @Override
    public <T extends StatsQueryDto> TimeRange resolve(T request, ZoneId zoneId) {
        Year lastYear = Year.now(zoneId).minusYears(1);
        LocalDate end = lastYear.atDay(lastYear.length());
        return new TimeRange(
                lastYear.atDay(1).atStartOfDay(),
                LocalDateTime.of(end, LocalTime.MAX),
                "去年"
        );
    }
}

