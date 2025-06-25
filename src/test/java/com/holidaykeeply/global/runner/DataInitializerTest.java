package com.holidaykeeply.global.runner;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import com.holidaykeeply.api.service.CountryInfoService;
import com.holidaykeeply.api.service.HolidayInfoService;
import com.holidaykeeply.domain.service.CountryService;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

	@Mock
	private CountryService countryService;

	@Mock
	private CountryInfoService countryInfoService;

	@Mock
	private HolidayInfoService holidayInfoService;

	@Mock
	private ApplicationArguments applicationArguments;

	@InjectMocks
	private DataInitializer dataInitializer;

	@Test
	@DisplayName("데이터가 없을 때 초기화를 수행한다.")
	void run_InitializeDataWhenEmpty() {
		// given
		given(countryService.count()).willReturn(Mono.just(0L));
		given(countryInfoService.initializeCountries()).willReturn(Mono.just(java.util.List.of()));
		given(holidayInfoService.initializeHolidays(any())).willReturn(Mono.empty());

		// when
		dataInitializer.run(applicationArguments);

		// then
		verify(countryService).count();
		verify(countryInfoService).initializeCountries();
		verify(holidayInfoService).initializeHolidays(any());
	}

	@Test
	@DisplayName("데이터가 이미 존재할 때 초기화를 건너뛴다.")
	void run_SkipInitializationWhenDataExists() {
		// given
		given(countryService.count()).willReturn(Mono.just(5L));

		// when
		dataInitializer.run(applicationArguments);

		// then
		verify(countryService).count();
		verify(countryInfoService, never()).initializeCountries();
		verify(holidayInfoService, never()).initializeHolidays(any());
	}

	@Test
	@DisplayName("count가 null일 때 초기화를 건너뛴다.")
	void run_SkipInitializationWhenCountIsNull() {
		// given
		given(countryService.count()).willReturn(Mono.empty());

		// when
		dataInitializer.run(applicationArguments);

		// then
		verify(countryService).count();
		verify(countryInfoService, never()).initializeCountries();
		verify(holidayInfoService, never()).initializeHolidays(any());
	}

	@Test
	@DisplayName("초기화 과정에서 에러가 발생해도 애플리케이션이 중단되지 않는다.")
	void run_HandleErrorDuringInitialization() {
		// given
		RuntimeException error = new RuntimeException("Initialization failed");
		given(countryService.count()).willReturn(Mono.just(0L));
		given(countryInfoService.initializeCountries()).willReturn(Mono.error(error));

		// when & then
		assertThatCode(() -> dataInitializer.run(applicationArguments))
			.doesNotThrowAnyException();

		verify(countryService).count();
		verify(countryInfoService).initializeCountries();
		verify(holidayInfoService, never()).initializeHolidays(any());
	}

	@Test
	@DisplayName("초기화가 성공적으로 완료된다.")
	void run_SuccessfulInitialization() {
		// given
		var countries = java.util.List.of(
			com.holidaykeeply.fixture.FixtureMonkeyUtils.getDefault().giveMeOne(com.holidaykeeply.domain.entity.Country.class)
		);

		given(countryService.count()).willReturn(Mono.just(0L));
		given(countryInfoService.initializeCountries()).willReturn(Mono.just(countries));
		given(holidayInfoService.initializeHolidays(countries)).willReturn(Mono.empty());

		// when
		dataInitializer.run(applicationArguments);

		// then
		verify(countryService).count();
		verify(countryInfoService).initializeCountries();
		verify(holidayInfoService).initializeHolidays(countries);
	}
} 