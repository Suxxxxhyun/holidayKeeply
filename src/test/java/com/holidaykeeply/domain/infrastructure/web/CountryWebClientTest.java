package com.holidaykeeply.domain.infrastructure.web;

import com.holidaykeeply.domain.entity.Country;
import com.holidaykeeply.fixture.FixtureMonkeyUtils;
import com.holidaykeeply.global.common.key.NagerApiProperties;
import com.holidaykeeply.global.error.exception.BusinessException;
import com.navercorp.fixturemonkey.FixtureMonkey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CountryWebClientTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private NagerApiProperties nagerApiProperties;

    @InjectMocks
    private CountryWebClient countryWebClient;

    private final FixtureMonkey fixtureMonkey = FixtureMonkeyUtils.getDefault();

    @Test
    @DisplayName("외부 API에서 국가 목록을 성공적으로 가져온다.")
    void fetchCountries_Success() {
        // given
        Country country1 = fixtureMonkey.giveMeBuilder(Country.class)
                .set("countryCode", "KR")
                .set("name", "South Korea")
                .sample();
        Country country2 = fixtureMonkey.giveMeBuilder(Country.class)
                .set("countryCode", "JP")
                .set("name", "Japan")
                .sample();

        List<Country> expectedCountries = List.of(country1, country2);

        given(nagerApiProperties.availableCountriesPath()).willReturn("/api/v3/AvailableCountries");
        given(webClient.get()).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.uri("/api/v3/AvailableCountries")).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.bodyToFlux(Country.class)).willReturn(Flux.fromIterable(expectedCountries));

        // when
        Mono<List<Country>> result = countryWebClient.fetchCountries();

        // then
        List<Country> actualCountries = result.block();
        assertThat(actualCountries).isEqualTo(expectedCountries);
    }

    @Test
    @DisplayName("외부 API 호출 시 WebClientResponseException이 발생하면 BusinessException을 던진다.")
    void fetchCountries_WebClientResponseException() {
        // given
        given(nagerApiProperties.availableCountriesPath()).willReturn("/api/v3/AvailableCountries");
        given(webClient.get()).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.uri("/api/v3/AvailableCountries")).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.bodyToFlux(Country.class))
                .willReturn(Flux.error(new WebClientResponseException(500, "Internal Server Error", null, null, null)));

        // when & then
        assertThatThrownBy(() -> countryWebClient.fetchCountries().block())
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("외부 API 호출 시 일반 Exception이 발생하면 BusinessException을 던진다.")
    void fetchCountries_GeneralException() {
        // given
        given(nagerApiProperties.availableCountriesPath()).willReturn("/api/v3/AvailableCountries");
        given(webClient.get()).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.uri("/api/v3/AvailableCountries")).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.bodyToFlux(Country.class))
                .willReturn(Flux.error(new RuntimeException("Network error")));

        // when & then
        assertThatThrownBy(() -> countryWebClient.fetchCountries().block())
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("빈 국가 목록을 반환한다.")
    void fetchCountries_EmptyList() {
        // given
        given(nagerApiProperties.availableCountriesPath()).willReturn("/api/v3/AvailableCountries");
        given(webClient.get()).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.uri("/api/v3/AvailableCountries")).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.bodyToFlux(Country.class)).willReturn(Flux.empty());

        // when
        Mono<List<Country>> result = countryWebClient.fetchCountries();

        // then
        List<Country> actualCountries = result.block();
        assertThat(actualCountries).isEmpty();
    }

    @Test
    @DisplayName("단일 국가만 반환한다.")
    void fetchCountries_SingleCountry() {
        // given
        Country country = fixtureMonkey.giveMeBuilder(Country.class)
                .set("countryCode", "US")
                .set("name", "United States")
                .sample();

        given(nagerApiProperties.availableCountriesPath()).willReturn("/api/v3/AvailableCountries");
        given(webClient.get()).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.uri("/api/v3/AvailableCountries")).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.bodyToFlux(Country.class)).willReturn(Flux.just(country));

        // when
        Mono<List<Country>> result = countryWebClient.fetchCountries();

        // then
        List<Country> actualCountries = result.block();
        assertThat(actualCountries).containsExactly(country);
    }
}