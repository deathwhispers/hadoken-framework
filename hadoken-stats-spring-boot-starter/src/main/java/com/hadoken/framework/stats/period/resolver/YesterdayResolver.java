package com.hadoken.framework.stats.period.resolver;

import com.hadoken.framework.stats.period.model.StatsQueryDto;
import com.hadoken.framework.stats.period.model.TimeRange;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/11 11:07
 */
public class YesterdayResolver implements PeriodResolver {
    @Override
    public <T extends StatsQueryDto> TimeRange resolve(T request, ZoneId zoneId) {
        LocalDate yesterday = LocalDate.now(zoneId).minusDays(1);
        return new TimeRange(
                LocalDateTime.of(yesterday, LocalTime.MIN),
                LocalDateTime.of(yesterday, LocalTime.MAX),
                "昨日"
        );
    }
}