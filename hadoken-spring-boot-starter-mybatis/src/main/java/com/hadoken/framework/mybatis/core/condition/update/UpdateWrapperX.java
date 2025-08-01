package com.hadoken.framework.mybatis.core.condition.update;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

import java.util.Collection;

/**
 * @author yanggj
 * @version 1.0.0
 * @date 2022/6/9 10:42
 */
public class UpdateWrapperX<T> extends UpdateWrapper<T> {

    public UpdateWrapperX<T> setIfPresent(String column, Object val) {
        if (ObjectUtil.isNotEmpty(val)) {
            super.set(column, val);
        }
        return this;
    }

    public UpdateWrapperX<T> likeIfPresent(String column, Object val) {
        if (ObjectUtil.isNotEmpty(val)) {
            return (UpdateWrapperX<T>) super.like(column, val);
        }
        return this;
    }

    public UpdateWrapperX<T> notLikeIfPresent(String column, Object val) {
        if (ObjectUtil.isNotEmpty(val)) {
            return (UpdateWrapperX<T>) super.notLike(column, val);
        }
        return this;
    }

    public UpdateWrapperX<T> likeLeftIfPresent(String column, Object val) {
        if (ObjectUtil.isNotEmpty(val)) {
            return (UpdateWrapperX<T>) super.likeLeft(column, val);
        }
        return this;
    }

    public UpdateWrapperX<T> likeRightIfPresent(String column, Object val) {
        if (ObjectUtil.isNotEmpty(val)) {
            return (UpdateWrapperX<T>) super.likeRight(column, val);
        }
        return this;
    }

    public UpdateWrapperX<T> inIfPresent(String column, Collection<?> values) {
        if (!CollectionUtils.isEmpty(values)) {
            return (UpdateWrapperX<T>) super.in(column, values);
        }
        return this;
    }

    public UpdateWrapperX<T> inIfPresent(String column, Object... values) {
        if (!ArrayUtils.isEmpty(values)) {
            return (UpdateWrapperX<T>) super.in(column, values);
        }
        return this;
    }

    public UpdateWrapperX<T> notInIfPresent(String column, Collection<?> values) {
        if (!CollectionUtils.isEmpty(values)) {
            return (UpdateWrapperX<T>) super.notIn(column, values);
        }
        return this;
    }

    public UpdateWrapperX<T> notInIfPresent(String column, Object... values) {
        if (!ArrayUtils.isEmpty(values)) {
            return (UpdateWrapperX<T>) super.notIn(column, values);
        }
        return this;
    }

    public UpdateWrapperX<T> eqIfPresent(String column, Object val) {
        if (val != null) {
            return (UpdateWrapperX<T>) super.eq(column, val);
        }
        return this;
    }

    public UpdateWrapperX<T> neIfPresent(String column, Object val) {
        if (val != null) {
            return (UpdateWrapperX<T>) super.ne(column, val);
        }
        return this;
    }

    public UpdateWrapperX<T> gtIfPresent(String column, Object val) {
        if (val != null) {
            return (UpdateWrapperX<T>) super.gt(column, val);
        }
        return this;
    }

    public UpdateWrapperX<T> geIfPresent(String column, Object val) {
        if (val != null) {
            return (UpdateWrapperX<T>) super.ge(column, val);
        }
        return this;
    }

    public UpdateWrapperX<T> ltIfPresent(String column, Object val) {
        if (val != null) {
            return (UpdateWrapperX<T>) super.lt(column, val);
        }
        return this;
    }

    // 小于等于
    public UpdateWrapperX<T> leIfPresent(String column, Object val) {
        if (val != null) {
            return (UpdateWrapperX<T>) super.le(column, val);
        }
        return this;
    }

    public UpdateWrapperX<T> betweenIfPresent(String column, Object val1, Object val2) {
        if (val1 != null && val2 != null) {
            return (UpdateWrapperX<T>) super.between(column, val1, val2);
        }
        if (val1 != null) {
            return (UpdateWrapperX<T>) ge(column, val1);
        }
        if (val2 != null) {
            return (UpdateWrapperX<T>) le(column, val2);
        }
        return this;
    }

    public UpdateWrapperX<T> notBetweenIfPresent(String column, Object val1, Object val2) {
        if (val1 != null && val2 != null) {
            return (UpdateWrapperX<T>) super.notBetween(column, val1, val2);
        }
        if (val1 != null) {
            return (UpdateWrapperX<T>) lt(column, val1);
        }
        if (val2 != null) {
            return (UpdateWrapperX<T>) gt(column, val2);
        }
        return this;
    }

    // ========== 重写父类方法，方便链式调用 ==========

    @Override
    public UpdateWrapperX<T> eq(boolean condition, String column, Object val) {
        super.eq(condition, column, val);
        return this;
    }

    @Override
    public UpdateWrapperX<T> eq(String column, Object val) {
        super.eq(column, val);
        return this;
    }

    @Override
    public UpdateWrapperX<T> orderByDesc(String column) {
        super.orderByDesc(column);
        return this;
    }

    @Override
    public UpdateWrapperX<T> orderByAsc(String column) {
        super.orderByAsc(column);
        return this;
    }

    @Override
    public UpdateWrapperX<T> groupBy(String column) {
        super.groupBy(column);
        return this;
    }

    @Override
    public UpdateWrapperX<T> last(String lastSql) {
        super.last(lastSql);
        return this;
    }

    @Override
    public UpdateWrapperX<T> in(String column, Collection<?> coll) {
        super.in(column, coll);
        return this;
    }

    @Override
    public UpdateWrapperX<T> or() {
        super.or();
        return this;
    }

}
