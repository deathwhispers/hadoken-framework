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
 * Created on 2025/8/11 11:06
 */
public class TodayResolver implements PeriodResolver {

    @Override
    public <T extends StatsQueryDto> TimeRange resolve(T request, ZoneId zoneId) {
        LocalDate now = LocalDate.now(zoneId);
        return new TimeRange(
                LocalDateTime.of(now, LocalTime.MIN),
                LocalDateTime.of(now, LocalTime.MAX),
                "今日"
        );
    }
}