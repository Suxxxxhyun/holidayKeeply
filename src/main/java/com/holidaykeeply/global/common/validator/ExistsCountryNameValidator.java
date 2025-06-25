package com.holidaykeeply.global.common.validator;

import org.springframework.stereotype.Component;

import com.holidaykeeply.domain.infrastructure.repository.CountryRepository;
import com.holidaykeeply.global.common.annotation.ExistsCountryName;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExistsCountryNameValidator implements ConstraintValidator<ExistsCountryName, String> {

	private final CountryRepository countryRepository;

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return value != null && countryRepository.existsByName(value);
	}
}
