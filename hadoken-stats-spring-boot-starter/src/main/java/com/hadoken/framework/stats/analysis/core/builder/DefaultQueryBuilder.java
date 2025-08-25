package com.hadoken.framework.stats.analysis.core.builder;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hadoken.framework.stats.analysis.core.dto.MetricSpec;
import com.hadoken.framework.stats.analysis.core.dto.QueryRequest;
import com.hadoken.framework.stats.period.model.TimeRange;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/24 16:59
 */
@Component
public class DefaultQueryBuilder implements QueryBuilder {

    @Override
    public QueryWrapper<Map<String, Object>> build(QueryRequest request, String timeColumn) {
        QueryWrapper<Map<String, Object>> wrapper = new QueryWrapper<>();

        // 1. SELECT
        wrapper.select(buildSelect(request.getGroupBy(), request.getMetrics()));

        // 2. WHERE
        applyFilters(wrapper, request.getFilters());
        applyTimeRange(wrapper, request.getTimeRange(), timeColumn);

        // 3. GROUP BY
        applyGroupBy(wrapper, request.getGroupBy());

        // 4. ORDER BY
        applyOrderBy(wrapper, request.getOrderBy());

        return wrapper;
    }

    private String[] buildSelect(List<String> groupBy, List<MetricSpec> metrics) {
        List<String> selectExpressions = new ArrayList<>();
        if (!CollectionUtils.isEmpty(groupBy)) {
            groupBy.forEach(dim -> selectExpressions.add(String.format("JSON_UNQUOTE(dimensions->'$.%s') AS %s", dim, dim)));
        }
        if (!CollectionUtils.isEmpty(metrics)) {
            metrics.forEach(metric -> {
                String alias = StringUtils.hasText(metric.getAlias()) ? metric.getAlias() : metric.getName();
                selectExpressions.add(
                        String.format("%s(CASE WHEN metric_name = '%s' THEN metric_value ELSE 0 END) AS %s",
                                metric.getAggType(), metric.getName(), alias)
                );
            });
        }
        return selectExpressions.toArray(new String[0]);
    }

    private void applyTimeRange(QueryWrapper<Map<String, Object>> wrapper, TimeRange timeRange, String timeColumn) {
        if (timeRange != null && StringUtils.hasText(timeRange.getStartDate()) && StringUtils.hasText(timeRange.getEndDate())) {
            wrapper.between(timeColumn, timeRange.getStartDate(), timeRange.getEndDate());
        }
    }

    private void applyFilters(QueryWrapper<Map<String, Object>> wrapper, List<Filter> filters) {
        if (CollectionUtils.isEmpty(filters)) {
            return;
        }
        filters.forEach(filter -> {
            String dbField = String.format("JSON_UNQUOTE(dimensions->'$.%s')", filter.getField());
            switch (filter.getOperator()) {
                case EQ:
                    wrapper.eq(dbField, filter.getValue());
                    break;
                case NE:
                    wrapper.ne(dbField, filter.getValue());
                    break;
                case GT:
                    wrapper.gt(dbField, filter.getValue());
                    break;
                case GTE:
                    wrapper.ge(dbField, filter.getValue());
                    break;
                case LT:
                    wrapper.lt(dbField, filter.getValue());
                    break;
                case LTE:
                    wrapper.le(dbField, filter.getValue());
                    break;
                case IN:
                    wrapper.in(dbField, (List) filter.getValue());
                    break;
                case NOT_IN:
                    wrapper.notIn(dbField, (List) filter.getValue());
                    break;
                case LIKE:
                    wrapper.like(dbField, filter.getValue());
                    break;
            }
        });
    }

    private void applyGroupBy(QueryWrapper<Map<String, Object>> wrapper, List<String> groupBy) {
        if (!CollectionUtils.isEmpty(groupBy)) {
            groupBy.forEach(dim -> wrapper.groupBy(String.format("JSON_UNQUOTE(dimensions->'$.%s')", dim)));
        }
    }

    private void applyOrderBy(QueryWrapper<Map<String, Object>> wrapper, List<OrderBy> orderBy) {
        if (CollectionUtils.isEmpty(orderBy)) {
            return;
        }
        orderBy.forEach(order -> {
            boolean isAsc = order.getDirection() == OrderBy.Direction.ASC;
            wrapper.orderBy(true, isAsc, order.getField());
        });
    }
}