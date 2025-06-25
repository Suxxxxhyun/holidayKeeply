package com.holidaykeeply.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.holidaykeeply.domain.entity.Country;
import com.holidaykeeply.domain.infrastructure.repository.CountryRepository;
import com.holidaykeeply.fixture.FixtureMonkeyUtils;

import reactor.core.publisher.Mono;

import com.navercorp.fixturemonkey.FixtureMonkey;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

	@InjectMocks
	private CountryService countryService;

	@Mock
	private CountryRepository countryRepository;

	@Test
	@DisplayName("전체 Country 개수를 반환한다.")
	void countCountries_Success() {
		// given
		given(countryRepository.count()).willReturn(5L);

		// when
		Mono<Long> result = countryService.count();

		// then
		assertEquals(5L, result.block());
	}

	@Test
	@DisplayName("countryCode로 Country를 조회한다.")
	void findByCountryCode_Success() {
		// given
		FixtureMonkey fixtureMonkey = FixtureMonkeyUtils.getDefault();

		Country country = fixtureMonkey.giveMeOne(Country.class);
		given(countryRepository.findByCountryCode("KR")).willReturn(Optional.of(country));

		// when
		Country result = countryService.findByCountryCode("KR");

		// then
		assertNotNull(result);
	}

	@Test
	@DisplayName("countryCode로 Country 조회 실패시 예외를 발생한다.")
	void findByCountryCodeError() {
		// given
		given(countryRepository.findByCountryCode("XX")).willReturn(Optional.empty());

		// when & then
		assertThrows(NoSuchElementException.class, () -> countryService.findByCountryCode("XX"));
	}

	@Test
	@DisplayName("여러 Country 저장")
	void saveAllCountries() {
		// given
		FixtureMonkey fixtureMonkey = FixtureMonkeyUtils.getDefault();

		Country country1 = fixtureMonkey.giveMeOne(Country.class);
		Country country2 = fixtureMonkey.giveMeOne(Country.class);
		List<Country> countries = Arrays.asList(country1, country2);
		given(countryRepository.saveAll(countries)).willReturn(countries);

		// when
		Mono<List<Country>> result = countryService.saveAll(countries);

		// then
		List<Country> saved = result.block();
		assertNotNull(saved);
		assertEquals(2, saved.size());
	}
}