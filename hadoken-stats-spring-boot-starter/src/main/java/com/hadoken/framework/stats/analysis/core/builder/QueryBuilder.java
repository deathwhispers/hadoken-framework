package com.hadoken.framework.stats.analysis.core.builder;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hadoken.framework.stats.analysis.core.dto.QueryRequest;

import java.util.Map;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/24 16:59
 */
public interface QueryBuilder {
    QueryWrapper<Map<String, Object>> build(QueryRequest request, String timeColumn);
}
