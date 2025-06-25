package com.holidaykeeply.global.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.holidaykeeply.global.common.validator.DateRangeValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = DateRangeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {
	String message() default "startDate는 endDate보다 이전이어야 합니다.";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
