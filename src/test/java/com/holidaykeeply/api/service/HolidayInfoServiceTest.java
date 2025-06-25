package com.holidaykeeply.api.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.holidaykeeply.api.dto.HolidayInfoDto;
import com.holidaykeeply.api.dto.SearchCondition;
import com.holidaykeeply.domain.entity.Country;
import com.holidaykeeply.domain.entity.Holiday;
import com.holidaykeeply.domain.infrastructure.web.HolidayWebClient;
import com.holidaykeeply.domain.service.CountryService;
import com.holidaykeeply.domain.service.HolidayService;
import com.holidaykeeply.fixture.FixtureMonkeyUtils;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import com.navercorp.fixturemonkey.FixtureMonkey;

@Slf4j
@ExtendWith(MockitoExtension.class)
class HolidayInfoServiceTest {

	@Spy
	@InjectMocks
	private HolidayInfoService holidayInfoService;

	@Mock
	private HolidayService holidayService;

	@Mock
	private CountryService countryService;

	@Mock
	private HolidayWebClient holidayWebClient;

	@Test
	@DisplayName("다중검색을 기반으로 공휴일을 페이징 기반으로 조회한다.")
	void getPageOfHolidays_Success() {
		FixtureMonkey fixtureMonkey = FixtureMonkeyUtils.getDefault();

		SearchCondition searchCondition = fixtureMonkey.giveMeBuilder(SearchCondition.class)
			.set("startDate", LocalDate.of(2025, 1, 1))
			.set("endDate", LocalDate.of(2025, 12, 31))
			.set("countryName", "Germany")
			.sample();

		Pageable pageable = PageRequest.of(0, 3);

		HolidayInfoDto.Response response = new HolidayInfoDto.Response(
			1L, "독일 통일의 날", "German Unity Day", "Germany",
			true, false, "1990", LocalDate.of(2025, 10, 3)
		);

		List<HolidayInfoDto.Response> content = Collections.singletonList(response);
		Page<HolidayInfoDto.Response> expect = new PageImpl<>(content, pageable, 1);

		given(holidayService.getHolidays(eq(searchCondition), any(Pageable.class)))
			.willReturn(expect);

		// when
		Page<HolidayInfoDto.Response> result = holidayInfoService.getHolidays(searchCondition, pageable);

		// then
		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		assertEquals("Germany", result.getContent().getFirst().getCountry());
	}

	@Test
	@DisplayName("연도와 국가명을 기반으로 공휴일을 삭제한다.")
	void deleteByYearAndCountry_Success() {
		// given
		final LocalDate year = LocalDate.of(2025,2,11);
		final String countryName = "Japan";

		FixtureMonkey fixtureMonkey = FixtureMonkeyUtils.getDefault();

		Country country = fixtureMonkey.giveMeOne(Country.class);

		given(countryService.findByCountryCode(eq(countryName))).willReturn(country);
		willDoNothing()
			.given(holidayService).deleteByDateAndCountry(year, country);

		// when
		holidayInfoService.deleteByDateAndCountry(year, countryName);

		// verify
		verify(holidayInfoService).deleteByDateAndCountry(year, countryName);

	}

	@Test
	@DisplayName("initializeHolidays: 여러 Country에 대해 년도별 upsertHoliday가 호출된다")
	void initializeHolidays_Success() {
		// given
		FixtureMonkey fixtureMonkey = FixtureMonkeyUtils.getDefault();

		Country country1 = fixtureMonkey.giveMeBuilder(Country.class)
			.set("countryCode", "KR")
			.sample();
		Country country2 = fixtureMonkey.giveMeBuilder(Country.class)
			.set("countryCode", "JP")
			.sample();

		List<Country> countries = Arrays.asList(country1, country2);

		// upsertHoliday는 Mono.empty() 반환하도록
		doReturn(Mono.empty()).when(holidayInfoService).upsertHoliday(any(LocalDate.class), anyString());

		// when
		Mono<Void> result = holidayInfoService.initializeHolidays(countries);

		// then
		assertThatCode(result::block).doesNotThrowAnyException();
		// 2개 국가 * 6개 년도 = 12번 호출
		verify(holidayInfoService, times(12)).upsertHoliday(any(LocalDate.class), anyString());
	}

	@Test
	@DisplayName("upsertHoliday: 외부 API에서 Holiday를 받아와 저장한다")
	void upsertHoliday_Success() {
		// given
		LocalDate year = LocalDate.of(2025, 1, 1);
		String countryCode = "KR";
		FixtureMonkey fixtureMonkey = FixtureMonkeyUtils.getDefault();

		Holiday holiday1 = fixtureMonkey.giveMeOne(Holiday.class);
		Holiday holiday2 = fixtureMonkey.giveMeOne(Holiday.class);
		List<Holiday> holidays = Arrays.asList(holiday1, holiday2);

		// HolidayWebClient Mock
		given(holidayWebClient.fetchHolidays(year.getYear(), countryCode))
			.willReturn(Mono.just(holidays));

		// HolidayService Mock
		given(holidayService.upsertAll(holidays, year, countryCode))
			.willReturn(Mono.empty());

		// when
		Mono<Void> result = holidayInfoService.upsertHoliday(year, countryCode);

		// then
		assertThatCode(result::block).doesNotThrowAnyException();

		verify(holidayWebClient).fetchHolidays(year.getYear(), countryCode);
		verify(holidayService).upsertAll(holidays, year, countryCode);
	}
}