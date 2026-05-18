package com.github.hadoken.framework.stats.period.resolver;

import com.github.hadoken.framework.stats.period.model.StatsQueryDto;
import com.github.hadoken.framework.stats.period.model.TimeRange;

import java.time.ZoneId;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/11 10:49
 */
public interface PeriodResolver {

    <T extends StatsQueryDto> TimeRange resolve(T request, ZoneId zoneId);

}
