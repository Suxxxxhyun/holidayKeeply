package com.holidaykeeply.api.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.holidaykeeply.api.dto.HolidayDeleteDto;
import com.holidaykeeply.api.dto.HolidayInfoDto;
import com.holidaykeeply.api.dto.HolidayUpsertDto;
import com.holidaykeeply.api.dto.SearchCondition;
import com.holidaykeeply.api.service.HolidayInfoService;
import com.holidaykeeply.api.swagger.HolidayInfoSwagger;
import com.holidaykeeply.global.common.response.Response;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/holidays")
@RequiredArgsConstructor
public class HolidayInfoController implements HolidayInfoSwagger {

	private final HolidayInfoService holidayInfoService;

	@GetMapping
	public Response<Page<HolidayInfoDto.Response>> getHolidays(
			@Valid @ModelAttribute final SearchCondition searchCondition,
			@PageableDefault(page = 1, size = 3) final Pageable pageable) {
		var holidays = holidayInfoService.getHolidays(searchCondition, pageable);
		return Response.ok(holidays);
	}

	@PutMapping("/upsert")
	public Response<String> upsert(
			@Valid @ModelAttribute final HolidayUpsertDto.Request request) {
		holidayInfoService.upsertHoliday(request.year(), request.countryCode()).block();
		return Response.ok(request.countryCode() + "가 수정되었습니다.");
	}

	@DeleteMapping("/{countryName}")
	public Response<String> deleteHolidays(
			@Valid @ModelAttribute final HolidayDeleteDto.Request request) {
		holidayInfoService.deleteByDateAndCountry(request.year(), request.countryName());
		return Response.ok("삭제되었습니다.");
	}
}
