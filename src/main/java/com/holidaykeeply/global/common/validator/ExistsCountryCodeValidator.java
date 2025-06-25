package com.holidaykeeply.global.common.validator;

import org.springframework.stereotype.Component;

import com.holidaykeeply.domain.infrastructure.repository.CountryRepository;
import com.holidaykeeply.global.common.annotation.ExistsCountryCode;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExistsCountryCodeValidator implements ConstraintValidator<ExistsCountryCode, String> {

	private final CountryRepository countryRepository;

	@Override
	public boolean isValid(
		final String value,
		final ConstraintValidatorContext context
	) {
		return value != null && countryRepository.existsByCountryCode(value);
	}
}
