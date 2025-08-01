package com.hadoken.framework.mybatis.core.condition.update;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * @author yanggj
 * @version 1.0.0
 * @date 2022/6/9 10:42
 */
public class LambdaUpdateWrapperX<T> extends LambdaUpdateWrapper<T> {

    public LambdaUpdateWrapperX<T> setIfPresent(SFunction<T, ?> column, Object val) {
        if (ObjectUtil.isNotEmpty(val)) {
            super.set(column, val);
        }
        return this;
    }

    public LambdaUpdateWrapperX<T> likeIfPresent(SFunction<T, ?> column, String val) {
        if (StringUtils.hasText(val)) {
            return (LambdaUpdateWrapperX<T>) super.like(column, val);
        }
        return this;
    }

    public LambdaUpdateWrapperX<T> inIfPresent(SFunction<T, ?> column, Collection<?> values) {
        if (!CollectionUtils.isEmpty(values)) {
            return (LambdaUpdateWrapperX<T>) super.in(column, values);
        }
        return this;
    }

    public LambdaUpdateWrapperX<T> inIfPresent(SFunction<T, ?> column, Object... values) {
        if (!ArrayUtils.isEmpty(values)) {
            return (LambdaUpdateWrapperX<T>) super.in(column, values);
        }
        return this;
    }

    public LambdaUpdateWrapperX<T> eqIfPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            return (LambdaUpdateWrapperX<T>) super.eq(column, val);
        }
        return this;
    }

    public LambdaUpdateWrapperX<T> neIfPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            return (LambdaUpdateWrapperX<T>) super.ne(column, val);
        }
        return this;
    }

    public LambdaUpdateWrapperX<T> gtIfPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            return (LambdaUpdateWrapperX<T>) super.gt(column, val);
        }
        return this;
    }

    public LambdaUpdateWrapperX<T> geIfPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            return (LambdaUpdateWrapperX<T>) super.ge(column, val);
        }
        return this;
    }

    public LambdaUpdateWrapperX<T> ltIfPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            return (LambdaUpdateWrapperX<T>) super.lt(column, val);
        }
        return this;
    }

    public LambdaUpdateWrapperX<T> leIfPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            return (LambdaUpdateWrapperX<T>) super.le(column, val);
        }
        return this;
    }

    public LambdaUpdateWrapperX<T> betweenIfPresent(SFunction<T, ?> column, Object val1, Object val2) {
        if (val1 != null && val2 != null) {
            return (LambdaUpdateWrapperX<T>) super.between(column, val1, val2);
        }
        if (val1 != null) {
            return (LambdaUpdateWrapperX<T>) ge(column, val1);
        }
        if (val2 != null) {
            return (LambdaUpdateWrapperX<T>) le(column, val2);
        }
        return this;
    }

    // ========== 重写父类方法，方便链式调用 ==========

    @Override
    public LambdaUpdateWrapperX<T> eq(boolean condition, SFunction<T, ?> column, Object val) {
        super.eq(condition, column, val);
        return this;
    }

    @Override
    public LambdaUpdateWrapperX<T> eq(SFunction<T, ?> column, Object val) {
        super.eq(column, val);
        return this;
    }

    @Override
    public LambdaUpdateWrapperX<T> last(String lastSql) {
        super.last(lastSql);
        return this;
    }

    @Override
    public LambdaUpdateWrapperX<T> in(SFunction<T, ?> column, Collection<?> coll) {
        super.in(column, coll);
        return this;
    }
}
