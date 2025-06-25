package com.holidaykeeply.api.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.holidaykeeply.domain.entity.Country;
import com.holidaykeeply.domain.provider.CountryProvider;
import com.holidaykeeply.domain.service.CountryService;
import com.holidaykeeply.fixture.FixtureMonkeyUtils;

import reactor.core.publisher.Mono;

import com.navercorp.fixturemonkey.FixtureMonkey;

@ExtendWith(MockitoExtension.class)
class CountryInfoServiceTest {

	@InjectMocks
	private CountryInfoService countryInfoService;

	@Mock
	private CountryProvider countryProvider;

	@Mock
	private CountryService countryService;

	@Test
	@DisplayName("AvailableCountries API에서 국가 목록을 받아와 저장한다")
	void initializeCountries_Success() {
		// given
		FixtureMonkey fixtureMonkey = FixtureMonkeyUtils.getDefault();

		Country country1 = fixtureMonkey.giveMeOne(Country.class);
		Country country2 = fixtureMonkey.giveMeOne(Country.class);
		List<Country> countryList = Arrays.asList(country1, country2);

		// CountryProvider Mock
		given(countryProvider.getCountries())
			.willReturn(Mono.just(countryList));

		// CountryService Mock
		given(countryService.saveAll(countryList))
			.willReturn(Mono.just(countryList));

		// when
		List<Country> result = countryInfoService.initializeCountries().block();

		// then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(2);
		assertThat(result).containsExactlyElementsOf(countryList);

		verify(countryProvider).getCountries();
		verify(countryService).saveAll(countryList);
	}

	@Test
	@DisplayName("CountryProvider에서 에러가 발생하면 에러를 전파한다")
	void initializeCountries_Error() {
		// given
		RuntimeException error = new RuntimeException("Provider Error");
		given(countryProvider.getCountries()).willReturn(Mono.error(error));

		// when & then
		assertThatThrownBy(() -> countryInfoService.initializeCountries().block())
			.isInstanceOf(RuntimeException.class)
			.hasMessage("Provider Error");

		verify(countryProvider).getCountries();
		verify(countryService, never()).saveAll(any());
	}

	@Test
	@DisplayName("빈 국가 목록을 처리한다")
	void initializeCountries_EmptyList() {
		// given
		List<Country> emptyList = List.of();
		given(countryProvider.getCountries()).willReturn(Mono.just(emptyList));
		given(countryService.saveAll(emptyList)).willReturn(Mono.just(emptyList));

		// when
		List<Country> result = countryInfoService.initializeCountries().block();

		// then
		assertThat(result).isEmpty();

		verify(countryProvider).getCountries();
		verify(countryService).saveAll(emptyList);
	}
}