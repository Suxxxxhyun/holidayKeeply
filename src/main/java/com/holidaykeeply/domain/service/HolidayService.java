package com.holidaykeeply.domain.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.holidaykeeply.api.dto.HolidayInfoDto;
import com.holidaykeeply.api.dto.SearchCondition;
import com.holidaykeeply.domain.entity.Country;
import com.holidaykeeply.domain.entity.Holiday;
import com.holidaykeeply.domain.infrastructure.repository.HolidayRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Transactional
@RequiredArgsConstructor
public class HolidayService {
	private final HolidayRepository holidayRepository;
	private final CountryService countryService;

	@Transactional(readOnly = true)
	public Page<HolidayInfoDto.Response> getHolidays(
			final SearchCondition searchCondition,
			final Pageable pageable) {
		return holidayRepository.findHolidaysByFilters(searchCondition, pageable);
	}

	public void deleteByDateAndCountry(final LocalDate year, final Country country) {
		holidayRepository.deleteByDateAndCountry(year, country);
	}

	public Mono<Void> upsertAll(
			final List<Holiday> holidays,
			final LocalDate year,
			final String countryCode) {
		return Mono.fromRunnable(() -> {
			var country = countryService.findByCountryCode(countryCode);
			holidays.forEach(holiday -> holiday.addCountry(year, country));
			holidayRepository.saveAll(holidays);
		}).subscribeOn(Schedulers.boundedElastic()).then();
	}
}
