package com.hadoken.framework.mybatis.core.condition.query;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

import java.util.Collection;

/**
 * @author yanggj
 * @version 1.0.0
 * @date 2022/5/11 9:16
 */
public class QueryWrapperX<T> extends QueryWrapper<T> {

    public QueryWrapperX<T> likeIfPresent(String column, Object val) {
        if (ObjectUtil.isNotEmpty(val)) {
            return (QueryWrapperX<T>) super.like(column, val);
        }
        return this;
    }

    public QueryWrapperX<T> notLikeIfPresent(String column, Object val) {
        if (ObjectUtil.isNotEmpty(val)) {
            return (QueryWrapperX<T>) super.notLike(column, val);
        }
        return this;
    }

    public QueryWrapperX<T> likeLeftIfPresent(String column, Object val) {
        if (ObjectUtil.isNotEmpty(val)) {
            return (QueryWrapperX<T>) super.likeLeft(column, val);
        }
        return this;
    }

    public QueryWrapperX<T> likeRightIfPresent(String column, Object val) {
        if (ObjectUtil.isNotEmpty(val)) {
            return (QueryWrapperX<T>) super.likeRight(column, val);
        }
        return this;
    }

    public QueryWrapperX<T> inIfPresent(String column, Collection<?> values) {
        if (!CollectionUtils.isEmpty(values)) {
            return (QueryWrapperX<T>) super.in(column, values);
        }
        return this;
    }

    public QueryWrapperX<T> inIfPresent(String column, Object... values) {
        if (!ArrayUtils.isEmpty(values)) {
            return (QueryWrapperX<T>) super.in(column, values);
        }
        return this;
    }

    public QueryWrapperX<T> notInIfPresent(String column, Collection<?> values) {
        if (!CollectionUtils.isEmpty(values)) {
            return (QueryWrapperX<T>) super.notIn(column, values);
        }
        return this;
    }

    public QueryWrapperX<T> notInIfPresent(String column, Object... values) {
        if (!ArrayUtils.isEmpty(values)) {
            return (QueryWrapperX<T>) super.notIn(column, values);
        }
        return this;
    }

    public QueryWrapperX<T> eqIfPresent(String column, Object val) {
        if (val != null) {
            return (QueryWrapperX<T>) super.eq(column, val);
        }
        return this;
    }

    public QueryWrapperX<T> neIfPresent(String column, Object val) {
        if (val != null) {
            return (QueryWrapperX<T>) super.ne(column, val);
        }
        return this;
    }

    public QueryWrapperX<T> gtIfPresent(String column, Object val) {
        if (val != null) {
            return (QueryWrapperX<T>) super.gt(column, val);
        }
        return this;
    }

    public QueryWrapperX<T> geIfPresent(String column, Object val) {
        if (val != null) {
            return (QueryWrapperX<T>) super.ge(column, val);
        }
        return this;
    }

    public QueryWrapperX<T> ltIfPresent(String column, Object val) {
        if (val != null) {
            return (QueryWrapperX<T>) super.lt(column, val);
        }
        return this;
    }

    // 小于等于
    public QueryWrapperX<T> leIfPresent(String column, Object val) {
        if (val != null) {
            return (QueryWrapperX<T>) super.le(column, val);
        }
        return this;
    }

    public QueryWrapperX<T> betweenIfPresent(String column, Object val1, Object val2) {
        if (val1 != null && val2 != null) {
            return (QueryWrapperX<T>) super.between(column, val1, val2);
        }
        if (val1 != null) {
            return (QueryWrapperX<T>) ge(column, val1);
        }
        if (val2 != null) {
            return (QueryWrapperX<T>) le(column, val2);
        }
        return this;
    }

    public QueryWrapperX<T> notBetweenIfPresent(String column, Object val1, Object val2) {
        if (val1 != null && val2 != null) {
            return (QueryWrapperX<T>) super.notBetween(column, val1, val2);
        }
        if (val1 != null) {
            return (QueryWrapperX<T>) lt(column, val1);
        }
        if (val2 != null) {
            return (QueryWrapperX<T>) gt(column, val2);
        }
        return this;
    }

    // ========== 重写父类方法，方便链式调用 ==========

    @Override
    public QueryWrapperX<T> eq(boolean condition, String column, Object val) {
        super.eq(condition, column, val);
        return this;
    }

    @Override
    public QueryWrapperX<T> eq(String column, Object val) {
        super.eq(column, val);
        return this;
    }

    @Override
    public QueryWrapperX<T> orderByDesc(String column) {
        super.orderByDesc(column);
        return this;
    }

    @Override
    public QueryWrapperX<T> orderByAsc(String column) {
        super.orderByAsc(column);
        return this;
    }

    @Override
    public QueryWrapperX<T> groupBy(String column) {
        super.groupBy(column);
        return this;
    }

    @Override
    public QueryWrapperX<T> last(String lastSql) {
        super.last(lastSql);
        return this;
    }

    @Override
    public QueryWrapperX<T> in(String column, Collection<?> coll) {
        super.in(column, coll);
        return this;
    }

    @Override
    public QueryWrapperX<T> or() {
        super.or();
        return this;
    }

}
