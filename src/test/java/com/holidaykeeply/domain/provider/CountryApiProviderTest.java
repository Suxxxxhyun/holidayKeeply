package com.holidaykeeply.domain.provider;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.holidaykeeply.domain.entity.Country;
import com.holidaykeeply.domain.infrastructure.web.CountryWebClient;
import com.holidaykeeply.domain.provider.impl.CountryApiProvider;
import com.holidaykeeply.fixture.FixtureMonkeyUtils;

import reactor.core.publisher.Mono;

import com.navercorp.fixturemonkey.FixtureMonkey;

@ExtendWith(MockitoExtension.class)
class CountryApiProviderTest {

	@Mock
	private CountryWebClient countryWebClient;

	@InjectMocks
	private CountryApiProvider countryApiProvider;

	private final FixtureMonkey fixtureMonkey = FixtureMonkeyUtils.getDefault();

	@Test
	@DisplayName("외부 API에서 국가 목록을 성공적으로 가져온다.")
	void getCountries_Success() {
		// given
		Country country1 = fixtureMonkey.giveMeBuilder(Country.class)
			.set("countryCode", "KR")
			.set("name", "South Korea")
			.sample();
		Country country2 = fixtureMonkey.giveMeBuilder(Country.class)
			.set("countryCode", "JP")
			.set("name", "Japan")
			.sample();
		Country country3 = fixtureMonkey.giveMeBuilder(Country.class)
			.set("countryCode", "US")
			.set("name", "United States")
			.sample();

		List<Country> expectedCountries = List.of(country1, country2, country3);

		given(countryWebClient.fetchCountries()).willReturn(Mono.just(expectedCountries));

		// when
		Mono<List<Country>> result = countryApiProvider.getCountries();

		// then
		List<Country> actualCountries = result.block();
		assertThat(actualCountries).isEqualTo(expectedCountries);

		verify(countryWebClient).fetchCountries();
	}

	@Test
	@DisplayName("빈 국가 목록을 반환한다.")
	void getCountries_EmptyList() {
		// given
		List<Country> emptyList = List.of();
		given(countryWebClient.fetchCountries()).willReturn(Mono.just(emptyList));

		// when
		Mono<List<Country>> result = countryApiProvider.getCountries();

		// then
		List<Country> actualCountries = result.block();
		assertThat(actualCountries).isEmpty();

		verify(countryWebClient).fetchCountries();
	}

	@Test
	@DisplayName("단일 국가만 반환한다.")
	void getCountries_SingleCountry() {
		// given
		Country country = fixtureMonkey.giveMeBuilder(Country.class)
			.set("countryCode", "DE")
			.set("name", "Germany")
			.sample();

		List<Country> singleCountryList = List.of(country);
		given(countryWebClient.fetchCountries()).willReturn(Mono.just(singleCountryList));

		// when
		Mono<List<Country>> result = countryApiProvider.getCountries();

		// then
		List<Country> actualCountries = result.block();
		assertThat(actualCountries).containsExactly(country);

		verify(countryWebClient).fetchCountries();
	}

	@Test
	@DisplayName("CountryWebClient에서 에러가 발생하면 에러를 전파한다.")
	void getCountries_Error() {
		// given
		RuntimeException error = new RuntimeException("API Error");
		given(countryWebClient.fetchCountries()).willReturn(Mono.error(error));

		// when & then
		assertThatThrownBy(() -> countryApiProvider.getCountries().block())
			.isInstanceOf(RuntimeException.class)
			.hasMessage("API Error");

		verify(countryWebClient).fetchCountries();
	}
} 