package com.holidaykeeply.domain.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.holidaykeeply.api.dto.HolidayInfoDto;
import com.holidaykeeply.api.dto.SearchCondition;

public interface HolidayCustomRepository {
	Page<HolidayInfoDto.Response> findHolidaysByFilters(
			final SearchCondition searchCondition,
			final Pageable pageable);
}
