package com.holidaykeeply.domain.infrastructure.repository;

import static com.holidaykeeply.domain.entity.QHoliday.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.holidaykeeply.api.dto.HolidayInfoDto;
import com.holidaykeeply.api.dto.QHolidayInfoDto_Response;
import com.holidaykeeply.api.dto.SearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class HolidayCustomRepositoryImpl implements HolidayCustomRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<HolidayInfoDto.Response> findHolidaysByFilters(
			final SearchCondition dto,
			final Pageable pageable) {

		List<HolidayInfoDto.Response> results = queryFactory
				.select(new QHolidayInfoDto_Response(
						holiday.id,
						holiday.localName,
						holiday.name,
						holiday.country.name,
						holiday.fixed,
						holiday.global,
						holiday.launchYear,
						holiday.date))
				.from(holiday)
				.where(
						dateBetween(dto.startDate(), dto.endDate()),
						countryNameEq(dto.countryName()))
				.orderBy(holiday.date.desc())
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		Long total = queryFactory
				.select(holiday.count())
				.from(holiday)
				.fetchOne();

		log.info("total = " + total);

		return new PageImpl<>(results, pageable, total);
	}

	private BooleanExpression countryNameEq(String countryName) {
		return countryName != null ? holiday.country.name.eq(countryName) : null;
	}

	private BooleanExpression dateBetween(LocalDate startDate, LocalDate endDate) {
		if (startDate != null && endDate != null) {
			return holiday.date.between(startDate, endDate);
		} else if (startDate != null) {
			return holiday.date.goe(startDate);
		} else if (endDate != null) {
			return holiday.date.loe(endDate);
		} else {
			return null; // 조건 없음
		}
	}
}
