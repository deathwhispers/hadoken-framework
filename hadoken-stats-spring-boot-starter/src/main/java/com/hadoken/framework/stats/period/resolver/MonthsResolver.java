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
 * Created on 2025/8/11 10:51
 */
public class MonthsResolver implements PeriodResolver {

    @Override
    public <T extends StatsQueryDto> TimeRange resolve(T request, ZoneId zoneId) {
        int n = request.getLastN();
        if (n <= 0) throw new IllegalArgumentException("lastN 必须大于 0");
        LocalDate end = LocalDate.now(zoneId);
        LocalDate begin = end.minusMonths(n);
        return new TimeRange(
                LocalDateTime.of(begin, LocalTime.MIN),
                LocalDateTime.of(end, LocalTime.MAX),
                "近" + n + "月");
    }
}