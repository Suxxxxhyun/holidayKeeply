package com.holidaykeeply.global.common.validator;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.holidaykeeply.domain.infrastructure.repository.CountryRepository;
import com.holidaykeeply.global.common.validator.ExistsCountryCodeValidator;

import jakarta.validation.ConstraintValidatorContext;

@ExtendWith(MockitoExtension.class)
class ExistsCountryCodeValidatorTest {

	@Mock
	private CountryRepository countryRepository;

	@Mock
	private ConstraintValidatorContext context;

	@InjectMocks
	private ExistsCountryCodeValidator validator;

	@Test
	@DisplayName("존재하는 국가 코드인 경우 유효하다.")
	void isValid_ExistingCountryCode() {
		// given
		String countryCode = "KR";
		given(countryRepository.existsByCountryCode(countryCode)).willReturn(true);

		// when
		boolean result = validator.isValid(countryCode, context);

		// then
		assertThat(result).isTrue();
		verify(countryRepository).existsByCountryCode(countryCode);
	}

	@Test
	@DisplayName("존재하지 않는 국가 코드인 경우 유효하지 않다.")
	void isValid_NonExistingCountryCode() {
		// given
		String countryCode = "XX";
		given(countryRepository.existsByCountryCode(countryCode)).willReturn(false);

		// when
		boolean result = validator.isValid(countryCode, context);

		// then
		assertThat(result).isFalse();
		verify(countryRepository).existsByCountryCode(countryCode);
	}

	@Test
	@DisplayName("null 값인 경우 유효하지 않다.")
	void isValid_NullValue() {
		// given
		String countryCode = null;

		// when
		boolean result = validator.isValid(countryCode, context);

		// then
		assertThat(result).isFalse();
		verify(countryRepository, never()).existsByCountryCode(any());
	}

	@Test
	@DisplayName("빈 문자열인 경우 유효하지 않다.")
	void isValid_EmptyString() {
		// given
		String countryCode = "";
		given(countryRepository.existsByCountryCode(countryCode)).willReturn(false);

		// when
		boolean result = validator.isValid(countryCode, context);

		// then
		assertThat(result).isFalse();
		verify(countryRepository).existsByCountryCode(countryCode);
	}

	@Test
	@DisplayName("공백 문자열인 경우 유효하지 않다.")
	void isValid_BlankString() {
		// given
		String countryCode = "   ";
		given(countryRepository.existsByCountryCode(countryCode)).willReturn(false);

		// when
		boolean result = validator.isValid(countryCode, context);

		// then
		assertThat(result).isFalse();
		verify(countryRepository).existsByCountryCode(countryCode);
	}

	@Test
	@DisplayName("다양한 국가 코드에 대해 정상 검증한다.")
	void isValid_VariousCountryCodes() {
		// given
		given(countryRepository.existsByCountryCode("US")).willReturn(true);
		given(countryRepository.existsByCountryCode("JP")).willReturn(true);
		given(countryRepository.existsByCountryCode("DE")).willReturn(true);
		given(countryRepository.existsByCountryCode("INVALID")).willReturn(false);

		// when & then
		assertThat(validator.isValid("US", context)).isTrue();
		assertThat(validator.isValid("JP", context)).isTrue();
		assertThat(validator.isValid("DE", context)).isTrue();
		assertThat(validator.isValid("INVALID", context)).isFalse();

		verify(countryRepository).existsByCountryCode("US");
		verify(countryRepository).existsByCountryCode("JP");
		verify(countryRepository).existsByCountryCode("DE");
		verify(countryRepository).existsByCountryCode("INVALID");
	}

	@Test
	@DisplayName("대소문자가 다른 국가 코드에 대해 정상 검증한다.")
	void isValid_CaseSensitiveCountryCodes() {
		// given
		given(countryRepository.existsByCountryCode("kr")).willReturn(false);
		given(countryRepository.existsByCountryCode("KR")).willReturn(true);

		// when & then
		assertThat(validator.isValid("kr", context)).isFalse();
		assertThat(validator.isValid("KR", context)).isTrue();

		verify(countryRepository).existsByCountryCode("kr");
		verify(countryRepository).existsByCountryCode("KR");
	}

	@Test
	@DisplayName("특수 문자가 포함된 국가 코드에 대해 정상 검증한다.")
	void isValid_SpecialCharactersInCountryCode() {
		// given
		String countryCodeWithSpecialChar = "K-R";
		given(countryRepository.existsByCountryCode(countryCodeWithSpecialChar)).willReturn(false);

		// when
		boolean result = validator.isValid(countryCodeWithSpecialChar, context);

		// then
		assertThat(result).isFalse();
		verify(countryRepository).existsByCountryCode(countryCodeWithSpecialChar);
	}
} 