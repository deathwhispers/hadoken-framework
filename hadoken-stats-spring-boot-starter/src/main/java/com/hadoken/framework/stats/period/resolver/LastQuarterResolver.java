package com.hadoken.framework.stats.period.resolver;

import com.hadoken.framework.stats.period.adjuster.QuarterAdjusters;
import com.hadoken.framework.stats.period.model.StatsQueryDto;
import com.hadoken.framework.stats.period.model.TimeRange;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/11 11:09
 */
public class LastQuarterResolver implements PeriodResolver {
    @Override
    public <T extends StatsQueryDto> TimeRange resolve(T request, ZoneId zoneId) {
        LocalDate now = LocalDate.now(zoneId);
        LocalDate start = now.with(QuarterAdjusters.firstDayOfPreviousQuarter());
        LocalDate end = now.with(QuarterAdjusters.lastDayOfPreviousQuarter());
        return new TimeRange(
                start.atStartOfDay(),
                LocalDateTime.of(end, LocalTime.MAX),
                "上季度"
        );
    }
}