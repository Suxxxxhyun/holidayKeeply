package com.holidaykeeply.api.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.holidaykeeply.api.dto.HolidayInfoDto;
import com.holidaykeeply.api.dto.SearchCondition;
import com.holidaykeeply.domain.entity.Country;
import com.holidaykeeply.domain.infrastructure.web.HolidayWebClient;
import com.holidaykeeply.domain.service.CountryService;
import com.holidaykeeply.domain.service.HolidayService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class HolidayInfoService {
	private final HolidayService holidayService;
	private final CountryService countryService;
	private final HolidayWebClient holidayWebClient;

	public Page<HolidayInfoDto.Response> getHolidays(
			final SearchCondition searchCondition,
			final Pageable pageable) {
		return holidayService.getHolidays(searchCondition, pageable);
	}

	public void deleteByDateAndCountry(
			final LocalDate year,
			final String countryName) {
		var country = countryService.findByCountryCode(countryName);
		holidayService.deleteByDateAndCountry(year, country);
	}

	public Mono<Void> upsertHoliday(
			final LocalDate year,
			final String countryCode) {
		return holidayWebClient.fetchHolidays(year.getYear(), countryCode)
				.flatMap(holidays -> holidayService.upsertAll(holidays, year, countryCode)) // DB 저장
				.then();
	}

	public Mono<Void> initializeHolidays(final List<Country> countries) {
		return Flux.fromIterable(countries) // 전달받은 리스트 사용
				.flatMap(country -> Flux.range(2020, 6) // 2020~2025 6개 년도
						.flatMap(year -> upsertHoliday(LocalDate.of(year, 1, 1), country.getCountryCode())) // 년도별로 공휴일 저장
				)
				.then()
				.subscribeOn(Schedulers.boundedElastic());
	}

	/**
	 * 지정된 연도들의 공휴일 데이터를 동기화
	 */
	public Mono<Void> syncHolidaysForYears(
			final List<Country> countries,
			final List<Integer> years) {
		return Flux.fromIterable(countries)
				.flatMap(country -> Flux.fromIterable(years)
						.flatMap(year -> {
							log.info("Syncing holiday data for country: {}, year: {}", country.getCountryCode(), year);
							return upsertHoliday(LocalDate.of(year, 1, 1), country.getCountryCode());
						}))
				.then()
				.subscribeOn(Schedulers.boundedElastic());
	}
}
