package com.holidaykeeply.domain.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.holidaykeeply.api.dto.HolidayInfoDto;
import com.holidaykeeply.api.dto.SearchCondition;
import com.holidaykeeply.domain.entity.Country;
import com.holidaykeeply.domain.entity.Holiday;
import com.holidaykeeply.domain.infrastructure.repository.CountryRepository;
import com.holidaykeeply.domain.infrastructure.repository.HolidayRepository;
import com.holidaykeeply.fixture.FixtureMonkeyUtils;
import com.holidaykeeply.global.config.QueryDslConfig;

import jakarta.persistence.EntityManager;

import com.navercorp.fixturemonkey.FixtureMonkey;

@DataJpaTest
@Import(QueryDslConfig.class)
class HolidayCustomRepositoryImplTest {

	@Autowired
	private HolidayRepository holidayRepository;

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private EntityManager em;

	@BeforeEach
	void setUp() {
		holidayRepository.deleteAllInBatch();
		countryRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("검색 조건에 따라 공휴일을 페이징으로 조회한다")
	void findHolidaysByFilters_Success() {
		// given
		FixtureMonkey fixtureMonkey = FixtureMonkeyUtils.getDefault();

		Country country = fixtureMonkey.giveMeBuilder(Country.class)
				.set("countryCode", "KR")
				.set("name", "대한민국")
				.set("id", null)
				.set("holidays", null) // 컬렉션 명시적으로 비움
				.sample();
		country = countryRepository.save(country);

		Holiday holiday = fixtureMonkey.giveMeBuilder(Holiday.class)
				.set("id", null)
				.set("country", country)
				.set("date", LocalDate.of(2025, 10, 3))
				.set("name", "개천절")
				.set("localName", "개천절")
				.set("holidays", null) // 컬렉션 명시적으로 비움
				.sample();

		holidayRepository.save(holiday);

		SearchCondition condition = SearchCondition.builder()
				.startDate(LocalDate.of(2025, 1, 1))
				.endDate(LocalDate.of(2025, 12, 31))
				.countryName("대한민국")
				.build();

		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<HolidayInfoDto.Response> result = holidayRepository.findHolidaysByFilters(condition, pageable);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).isNotEmpty();
		assertThat(result.getContent().getFirst().getCountry()).isEqualTo("대한민국");
	}

	@AfterEach
	void cleanUp() {
		holidayRepository.deleteAllInBatch();
		countryRepository.deleteAllInBatch();
	}
}