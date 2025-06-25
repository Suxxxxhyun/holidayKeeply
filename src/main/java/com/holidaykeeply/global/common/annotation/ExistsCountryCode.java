package com.holidaykeeply.global.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.holidaykeeply.global.common.validator.ExistsCountryCodeValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = ExistsCountryCodeValidator.class)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistsCountryCode {
	String message() default "존재하지 않는 국가 코드입니다.";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
