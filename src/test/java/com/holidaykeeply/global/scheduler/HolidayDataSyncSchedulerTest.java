package com.holidaykeeply.global.scheduler;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.holidaykeeply.api.service.HolidayInfoService;
import com.holidaykeeply.domain.entity.Country;
import com.holidaykeeply.domain.provider.CountryProvider;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class HolidayDataSyncSchedulerTest {

	@Mock
	private CountryProvider countryProvider;

	@Mock
	private HolidayInfoService holidayInfoService;

	@InjectMocks
	private HolidayDataSyncScheduler holidayDataSyncScheduler;

	private List<Country> testCountries;

	@BeforeEach
	void setUp() {
		testCountries = List.of(
			com.holidaykeeply.fixture.FixtureMonkeyUtils.getDefault().giveMeOne(Country.class),
			com.holidaykeeply.fixture.FixtureMonkeyUtils.getDefault().giveMeOne(Country.class)
		);
	}

	@Test
	@DisplayName("공휴일 데이터 동기화가 성공적으로 완료된다.")
	void syncHolidayData_Success() {
		// given
		int currentYear = LocalDate.now().getYear();
		int previousYear = currentYear - 1;
		List<Integer> yearsToSync = List.of(previousYear, currentYear);

		given(countryProvider.getCountries()).willReturn(Mono.just(testCountries));
		given(holidayInfoService.syncHolidaysForYears(testCountries, yearsToSync))
			.willReturn(Mono.empty());

		// when
		holidayDataSyncScheduler.syncHolidayData();

		// then
		verify(countryProvider).getCountries();
		verify(holidayInfoService).syncHolidaysForYears(testCountries, yearsToSync);
	}

	@Test
	@DisplayName("동기화 과정에서 올바른 연도 범위가 사용된다.")
	void syncHolidayData_CorrectYearRange() {
		// given
		int currentYear = LocalDate.now().getYear();
		int previousYear = currentYear - 1;
		List<Integer> expectedYears = List.of(previousYear, currentYear);

		given(countryProvider.getCountries()).willReturn(Mono.just(testCountries));
		given(holidayInfoService.syncHolidaysForYears(testCountries, expectedYears))
			.willReturn(Mono.empty());

		// when
		holidayDataSyncScheduler.syncHolidayData();

		// then
		verify(holidayInfoService).syncHolidaysForYears(testCountries, expectedYears);
	}
}