package com.hadoken.framework.stats.period.resolver;

import com.hadoken.framework.stats.period.model.StatsQueryDto;
import com.hadoken.framework.stats.period.model.TimeRange;

import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/11 11:08
 */
public class LastMonthResolver implements PeriodResolver {
    @Override
    public <T extends StatsQueryDto> TimeRange resolve(T request, ZoneId zoneId) {
        YearMonth lastMonth = YearMonth.now(zoneId).minusMonths(1);
        return new TimeRange(
                lastMonth.atDay(1).atStartOfDay(),
                lastMonth.atEndOfMonth().atTime(LocalTime.MAX),
                "上月"
        );
    }
}
