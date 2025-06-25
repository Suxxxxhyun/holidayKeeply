package com.holidaykeeply.global.common.validator;

import com.holidaykeeply.api.dto.SearchCondition;
import com.holidaykeeply.global.common.annotation.ValidDateRange;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, SearchCondition> {
	@Override
	public boolean isValid(
		final SearchCondition value,
		final ConstraintValidatorContext context
	) {
		if (value.startDate() == null || value.endDate() == null) {
			return true; // @NotNull로 이미 검증됨
		}
		return value.startDate().isBefore(value.endDate()) || value.startDate().isEqual(value.endDate());
	}
}
