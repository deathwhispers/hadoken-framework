package com.github.hadoken.framework.stats.period.validator;

import com.github.hadoken.framework.stats.period.enums.TemporalUnitEnum;
import com.github.hadoken.framework.stats.period.validator.constraints.TemporalUnitConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


/**
 * 时间单位校验器
 * {@link TemporalUnitConstraint}
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/11 09:27
 */
public class TemporalUnitValidator implements ConstraintValidator<TemporalUnitConstraint, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return TemporalUnitEnum.contains(value);
    }
}
