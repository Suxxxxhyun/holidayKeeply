package com.holidaykeeply.global.common.validator;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.holidaykeeply.api.dto.SearchCondition;
import com.holidaykeeply.global.common.validator.DateRangeValidator;

import jakarta.validation.ConstraintValidatorContext;

@ExtendWith(MockitoExtension.class)
class DateRangeValidatorTest {

	@Mock
	private ConstraintValidatorContext context;

	private final DateRangeValidator validator = new DateRangeValidator();

	@Test
	@DisplayName("시작 날짜가 종료 날짜보다 이전인 경우 유효하다.")
	void isValid_StartDateBeforeEndDate() {
		// given
		LocalDate startDate = LocalDate.of(2025, 1, 1);
		LocalDate endDate = LocalDate.of(2025, 12, 31);
		SearchCondition searchCondition = SearchCondition.builder()
				.startDate(LocalDate.of(2025, 1, 1))
				.endDate(LocalDate.of(2025, 12, 31))
				.countryName("KR")
				.build();

		// when
		boolean result = validator.isValid(searchCondition, context);

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("시작 날짜와 종료 날짜가 같은 경우 유효하다.")
	void isValid_StartDateEqualsEndDate() {
		// given
		LocalDate date = LocalDate.of(2025, 6, 15);
		SearchCondition searchCondition = SearchCondition.builder()
				.startDate(date)
				.endDate(date)
				.countryName("KR")
				.build();

		// when
		boolean result = validator.isValid(searchCondition, context);

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("시작 날짜가 종료 날짜보다 이후인 경우 유효하지 않다.")
	void isValid_StartDateAfterEndDate() {
		// given
		LocalDate startDate = LocalDate.of(2025, 12, 31);
		LocalDate endDate = LocalDate.of(2025, 1, 1);
		SearchCondition searchCondition = SearchCondition.builder()
				.startDate(startDate)
				.endDate(endDate)
				.countryName("KR")
				.build();

		// when
		boolean result = validator.isValid(searchCondition, context);

		// then
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("시작 날짜가 null인 경우 유효하다.")
	void isValid_StartDateIsNull() {
		// given
		LocalDate endDate = LocalDate.of(2025, 12, 31);
		SearchCondition searchCondition = SearchCondition.builder()
				.startDate(null)
				.endDate(endDate)
				.countryName("KR")
				.build();

		// when
		boolean result = validator.isValid(searchCondition, context);

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("종료 날짜가 null인 경우 유효하다.")
	void isValid_EndDateIsNull() {
		// given
		LocalDate startDate = LocalDate.of(2025, 1, 1);
		SearchCondition searchCondition = SearchCondition.builder()
				.startDate(startDate)
				.endDate(null)
				.countryName("KR")
				.build();

		// when
		boolean result = validator.isValid(searchCondition, context);

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("시작 날짜와 종료 날짜가 모두 null인 경우 유효하다.")
	void isValid_BothDatesAreNull() {
		// given
		SearchCondition searchCondition = SearchCondition.builder()
				.startDate(null)
				.endDate(null)
				.countryName("KR")
				.build();

		// when
		boolean result = validator.isValid(searchCondition, context);

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("연도가 다른 경우에도 정상 검증한다.")
	void isValid_DifferentYears() {
		// given
		LocalDate startDate = LocalDate.of(2024, 12, 31);
		LocalDate endDate = LocalDate.of(2025, 1, 1);
		SearchCondition searchCondition = SearchCondition.builder()
				.startDate(startDate)
				.endDate(endDate)
				.countryName("KR")
				.build();

		// when
		boolean result = validator.isValid(searchCondition, context);

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("같은 연도의 다른 월에 대해 정상 검증한다.")
	void isValid_SameYearDifferentMonths() {
		// given
		LocalDate startDate = LocalDate.of(2025, 3, 1);
		LocalDate endDate = LocalDate.of(2025, 8, 15);
		SearchCondition searchCondition = SearchCondition.builder()
				.startDate(startDate)
				.endDate(endDate)
				.countryName("KR")
				.build();

		// when
		boolean result = validator.isValid(searchCondition, context);

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("같은 월의 다른 일에 대해 정상 검증한다.")
	void isValid_SameMonthDifferentDays() {
		// given
		LocalDate startDate = LocalDate.of(2025, 6, 1);
		LocalDate endDate = LocalDate.of(2025, 6, 30);
		SearchCondition searchCondition = SearchCondition.builder()
				.startDate(startDate)
				.endDate(endDate)
				.countryName("KR")
				.build();

		// when
		boolean result = validator.isValid(searchCondition, context);

		// then
		assertThat(result).isTrue();
	}
} 