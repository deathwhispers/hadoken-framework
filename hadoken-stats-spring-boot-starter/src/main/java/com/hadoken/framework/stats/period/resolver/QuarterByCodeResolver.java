package com.hadoken.framework.stats.period.resolver;

import com.hadoken.framework.stats.period.model.StatsQueryDto;
import com.hadoken.framework.stats.period.model.TimeRange;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/11 10:58
 */
@Component
public class QuarterByCodeResolver implements PeriodResolver {

    @Override
    public <T extends StatsQueryDto> TimeRange resolve(T request, ZoneId zoneId) {
        String value = request.getValue();
        if (value == null || !value.toUpperCase().matches("\\d{4}-Q[1-4]")) {
            throw new IllegalArgumentException("季度格式错误，应为 YYYY-Q1~Q4");
        }
        String[] parts = value.toUpperCase().split("-Q");
        int year = Integer.parseInt(parts[0]);
        int quarter = Integer.parseInt(parts[1]);

        int firstMonth = (quarter - 1) * 3 + 1;
        LocalDate start = LocalDate.of(year, firstMonth, 1);
        LocalDate end = start.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());

        return new TimeRange(
                start.atStartOfDay(),
                LocalDateTime.of(end, LocalTime.MAX),
                value.toUpperCase()
        );
    }
}