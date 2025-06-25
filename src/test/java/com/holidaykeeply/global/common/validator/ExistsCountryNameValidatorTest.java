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
import com.holidaykeeply.global.common.validator.ExistsCountryNameValidator;

import jakarta.validation.ConstraintValidatorContext;

@ExtendWith(MockitoExtension.class)
class ExistsCountryNameValidatorTest {

	@Mock
	private CountryRepository countryRepository;

	@Mock
	private ConstraintValidatorContext context;

	@InjectMocks
	private ExistsCountryNameValidator validator;

	@Test
	@DisplayName("존재하는 국가명인 경우 유효하다.")
	void isValid_ExistingCountryName() {
		// given
		String countryName = "South Korea";
		given(countryRepository.existsByName(countryName)).willReturn(true);

		// when
		boolean result = validator.isValid(countryName, context);

		// then
		assertThat(result).isTrue();
		verify(countryRepository).existsByName(countryName);
	}

	@Test
	@DisplayName("존재하지 않는 국가명인 경우 유효하지 않다.")
	void isValid_NonExistingCountryName() {
		// given
		String countryName = "NonExistentCountry";
		given(countryRepository.existsByName(countryName)).willReturn(false);

		// when
		boolean result = validator.isValid(countryName, context);

		// then
		assertThat(result).isFalse();
		verify(countryRepository).existsByName(countryName);
	}

	@Test
	@DisplayName("null 값인 경우 유효하지 않다.")
	void isValid_NullValue() {
		// given
		String countryName = null;

		// when
		boolean result = validator.isValid(countryName, context);

		// then
		assertThat(result).isFalse();
		verify(countryRepository, never()).existsByName(any());
	}

	@Test
	@DisplayName("빈 문자열인 경우 유효하지 않다.")
	void isValid_EmptyString() {
		// given
		String countryName = "";
		given(countryRepository.existsByName(countryName)).willReturn(false);

		// when
		boolean result = validator.isValid(countryName, context);

		// then
		assertThat(result).isFalse();
		verify(countryRepository).existsByName(countryName);
	}

	@Test
	@DisplayName("공백 문자열인 경우 유효하지 않다.")
	void isValid_BlankString() {
		// given
		String countryName = "   ";
		given(countryRepository.existsByName(countryName)).willReturn(false);

		// when
		boolean result = validator.isValid(countryName, context);

		// then
		assertThat(result).isFalse();
		verify(countryRepository).existsByName(countryName);
	}

	@Test
	@DisplayName("다양한 국가명에 대해 정상 검증한다.")
	void isValid_VariousCountryNames() {
		// given
		given(countryRepository.existsByName("United States")).willReturn(true);
		given(countryRepository.existsByName("Japan")).willReturn(true);
		given(countryRepository.existsByName("Germany")).willReturn(true);
		given(countryRepository.existsByName("NonExistentCountry")).willReturn(false);

		// when & then
		assertThat(validator.isValid("United States", context)).isTrue();
		assertThat(validator.isValid("Japan", context)).isTrue();
		assertThat(validator.isValid("Germany", context)).isTrue();
		assertThat(validator.isValid("NonExistentCountry", context)).isFalse();

		verify(countryRepository).existsByName("United States");
		verify(countryRepository).existsByName("Japan");
		verify(countryRepository).existsByName("Germany");
		verify(countryRepository).existsByName("NonExistentCountry");
	}

	@Test
	@DisplayName("대소문자가 다른 국가명에 대해 정상 검증한다.")
	void isValid_CaseSensitiveCountryNames() {
		// given
		given(countryRepository.existsByName("south korea")).willReturn(false);
		given(countryRepository.existsByName("South Korea")).willReturn(true);

		// when & then
		assertThat(validator.isValid("south korea", context)).isFalse();
		assertThat(validator.isValid("South Korea", context)).isTrue();

		verify(countryRepository).existsByName("south korea");
		verify(countryRepository).existsByName("South Korea");
	}

	@Test
	@DisplayName("특수 문자가 포함된 국가명에 대해 정상 검증한다.")
	void isValid_SpecialCharactersInCountryName() {
		// given
		String countryNameWithSpecialChar = "Côte d'Ivoire";
		given(countryRepository.existsByName(countryNameWithSpecialChar)).willReturn(true);

		// when
		boolean result = validator.isValid(countryNameWithSpecialChar, context);

		// then
		assertThat(result).isTrue();
		verify(countryRepository).existsByName(countryNameWithSpecialChar);
	}

	@Test
	@DisplayName("긴 국가명에 대해 정상 검증한다.")
	void isValid_LongCountryName() {
		// given
		String longCountryName = "The United Kingdom of Great Britain and Northern Ireland";
		given(countryRepository.existsByName(longCountryName)).willReturn(true);

		// when
		boolean result = validator.isValid(longCountryName, context);

		// then
		assertThat(result).isTrue();
		verify(countryRepository).existsByName(longCountryName);
	}

	@Test
	@DisplayName("숫자가 포함된 국가명에 대해 정상 검증한다.")
	void isValid_CountryNameWithNumbers() {
		// given
		String countryNameWithNumbers = "United States of America 2024";
		given(countryRepository.existsByName(countryNameWithNumbers)).willReturn(false);

		// when
		boolean result = validator.isValid(countryNameWithNumbers, context);

		// then
		assertThat(result).isFalse();
		verify(countryRepository).existsByName(countryNameWithNumbers);
	}
} 