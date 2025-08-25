package com.hadoken.framework.stats.period.validator.constraints;

import com.hadoken.framework.stats.period.validator.TemporalUnitValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * 时间单位约束
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/11 09:27
 */
@Documented
@Constraint(validatedBy = {TemporalUnitValidator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface TemporalUnitConstraint {

    String message() default "{com.rhy.framework.statsperiod.validator.constraints.TemporalUnitConstraint.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
