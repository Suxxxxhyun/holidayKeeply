package com.holidaykeeply.api.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.holidaykeeply.api.dto.HolidayInfoDto;
import com.holidaykeeply.api.dto.SearchCondition;
import com.holidaykeeply.api.service.HolidayInfoService;
import com.holidaykeeply.domain.infrastructure.repository.CountryRepository;

import reactor.core.publisher.Mono;

@WebMvcTest(HolidayInfoController.class)
@AutoConfigureMockMvc(addFilters = false)
class HolidayInfoControllerTest {

	@MockitoBean
	private HolidayInfoService holidayInfoService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private CountryRepository countryRepository;

	@BeforeEach
	void setup() {
		when(countryRepository.existsByName(anyString())).thenReturn(true);
		when(countryRepository.existsByCountryCode("KR")).thenReturn(true);
	}

	@Test
	@DisplayName("여러 조건을 기반으로 공휴을일 조회한다.")
	void getHolidays() throws Exception {
		// given
		HolidayInfoDto.Response response = new HolidayInfoDto.Response(
				1L, "독일 통일의 날", "German Unity Day", "Germany",
				true, false, "1990", LocalDate.of(2025, 10, 3));

		List<HolidayInfoDto.Response> content = Collections.singletonList(response);
		Page<HolidayInfoDto.Response> page = new PageImpl<>(content, PageRequest.of(0, 3), 1);

		given(holidayInfoService.getHolidays(any(SearchCondition.class), any(Pageable.class)))
				.willReturn(page);

		// when & then
		mockMvc.perform(get("/api/v1/holidays")
				.param("startDate", "2025-01-01")
				.param("endDate", "2025-12-31")
				.param("countryName", "Germany")
				.param("page", "0")
				.param("size", "3"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.content[0].country").value("Germany"));
	}

	@Test
	@DisplayName("연도와 국가명을 기반으로 공휴일을 삭제한다.")
	void deleteHolidays() throws Exception {
		// given
		LocalDate year = LocalDate.of(2025, 2, 11);
		String countryName = "Japan";
		String formattedYear = LocalDate.of(2025, 2, 11).format(DateTimeFormatter.ISO_LOCAL_DATE);

		// when & then
		mockMvc.perform(
				delete("/api/v1/holidays/{countryName}", countryName)
						.param("year", formattedYear))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data").value("삭제되었습니다."));

		verify(holidayInfoService).deleteByDateAndCountry(year, countryName);

	}

	@Test
	@DisplayName("공휴일 데이터를 덮어쓰기한다.")
	void testUpsert() throws Exception {
		given(holidayInfoService.upsertHoliday(any(LocalDate.class), anyString()))
				.willReturn(Mono.empty());

		mockMvc.perform(put("/api/v1/holidays/upsert")
				.param("year", "2025-01-01")
				.param("countryCode", "KR"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data").value("KR가 수정되었습니다."));
	}
}