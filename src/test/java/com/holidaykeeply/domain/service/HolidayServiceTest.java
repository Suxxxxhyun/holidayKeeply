package com.holidaykeeply.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.holidaykeeply.api.dto.HolidayInfoDto;
import com.holidaykeeply.api.dto.SearchCondition;
import com.holidaykeeply.domain.infrastructure.repository.HolidayRepository;

@ExtendWith(MockitoExtension.class)
class HolidayServiceTest {

	@InjectMocks
	private HolidayService holidayService;

	@Mock
	private HolidayRepository holidayRepository;

	@Test
	@DisplayName("다중검색을 기반으로 공휴일을 페이징 기반으로 조회한다.")
	void getPageOfHolidays_Success() {
		// given
		SearchCondition searchCondition = SearchCondition.builder()
				.startDate(LocalDate.of(2025, 1, 1))
				.endDate(LocalDate.of(2025, 12, 31))
				.countryName("Germany")
				.build();

		Pageable pageable = PageRequest.of(0, 10);

		HolidayInfoDto.Response response = new HolidayInfoDto.Response(
				1L, "독일 통일의 날", "German Unity Day", "Germany",
				true, false, "1990", LocalDate.of(2025, 10, 3));

		List<HolidayInfoDto.Response> content = Collections.singletonList(response);
		Page<HolidayInfoDto.Response> expectedPage = new PageImpl<>(content, pageable, 1);

		given(holidayRepository.findHolidaysByFilters(any(SearchCondition.class), any(Pageable.class)))
				.willReturn(expectedPage);

		// when
		Page<HolidayInfoDto.Response> result = holidayService.getHolidays(searchCondition, pageable);

		// then
		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		assertEquals("German Unity Day", result.getContent().getFirst().getName());
		assertEquals("Germany", result.getContent().getFirst().getCountry());
	}
}