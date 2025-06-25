package com.holidaykeeply.api.swagger;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import com.holidaykeeply.api.dto.HolidayDeleteDto;
import com.holidaykeeply.api.dto.HolidayInfoDto;
import com.holidaykeeply.api.dto.HolidayUpsertDto;
import com.holidaykeeply.api.dto.SearchCondition;
import com.holidaykeeply.global.common.response.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@Tag(name = "Holiday API", description = "공휴일 정보 관련 API")
public interface HolidayInfoSwagger {

	@Operation(summary = "공휴일 목록 조회", description = "검색 조건에 따라 공휴일 목록을 페이징으로 조회합니다.")
	@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = HolidayInfoDto.Response.class)))
	Response<Page<HolidayInfoDto.Response>> getHolidays(
			@Parameter(description = "검색 조건") final SearchCondition searchCondition,
			@Parameter(description = "페이징 정보 (기본값: page=1, size=3)") @PageableDefault(page = 1, size = 3) final Pageable pageable);

	@Operation(summary = "공휴일 데이터 동기화 (테스트용)", description = "지정된 연도와 국가의 공휴일 데이터를 외부 API에서 동기화합니다.")
	@ApiResponse(responseCode = "200", description = "동기화 성공")
	Response<String> upsert(
			final HolidayUpsertDto.Request request);

	@Operation(summary = "공휴일 데이터 삭제", description = "지정된 연도와 국가의 공휴일 데이터를 삭제합니다.")
	@ApiResponse(responseCode = "200", description = "삭제 성공")
	Response<String> deleteHolidays(
			final HolidayDeleteDto.Request request);
}
