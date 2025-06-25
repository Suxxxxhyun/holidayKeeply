package com.holidaykeeply.domain.infrastructure.web;

import com.holidaykeeply.domain.entity.Country;
import com.holidaykeeply.domain.entity.Holiday;
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

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class HolidayWebClientTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private NagerApiProperties nagerApiProperties;

    @InjectMocks
    private HolidayWebClient holidayWebClient;

    private final FixtureMonkey fixtureMonkey = FixtureMonkeyUtils.getDefault();

    @Test
    @DisplayName("외부 API에서 공휴일 목록을 성공적으로 가져온다.")
    void fetchHolidays_Success() {
        // given
        Country country = fixtureMonkey.giveMeBuilder(Country.class)
                .set("countryCode", "KR")
                .set("name", "South Korea")
                .sample();

        Holiday holiday1 = fixtureMonkey.giveMeBuilder(Holiday.class)
                .set("name", "신정")
                .set("localName", "New Year's Day")
                .set("country", country)
                .set("date", LocalDate.of(2025, 1, 1))
                .set("fixed", true)
                .set("global", false)
                .set("launchYear", "1948")
                .sample();

        Holiday holiday2 = fixtureMonkey.giveMeBuilder(Holiday.class)
                .set("name", "삼일절")
                .set("localName", "Independence Movement Day")
                .set("country", country)
                .set("date", LocalDate.of(2025, 3, 1))
                .set("fixed", true)
                .set("global", false)
                .set("launchYear", "1919")
                .sample();

        List<Holiday> expectedHolidays = List.of(holiday1, holiday2);
        int year = 2025;
        String countryCode = "KR";

        given(nagerApiProperties.publicHolidaysPath()).willReturn("/api/v3/PublicHolidays/{year}/{countryCode}");
        given(webClient.get()).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.uri("/api/v3/PublicHolidays/2025/KR", year, countryCode)).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.bodyToFlux(Holiday.class)).willReturn(Flux.fromIterable(expectedHolidays));

        // when
        Mono<List<Holiday>> result = holidayWebClient.fetchHolidays(year, countryCode);

        // then
        List<Holiday> actualHolidays = result.block();
        assertThat(actualHolidays).isEqualTo(expectedHolidays);
    }

    @Test
    @DisplayName("외부 API 호출 시 WebClientResponseException이 발생하면 BusinessException을 던진다.")
    void fetchHolidays_WebClientResponseException() {
        // given
        int year = 2025;
        String countryCode = "JP";

        given(nagerApiProperties.publicHolidaysPath()).willReturn("/api/v3/PublicHolidays/{year}/{countryCode}");
        given(webClient.get()).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.uri("/api/v3/PublicHolidays/2025/JP", year, countryCode)).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.bodyToFlux(Holiday.class))
                .willReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        // when & then
        assertThatThrownBy(() -> holidayWebClient.fetchHolidays(year, countryCode).block())
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("외부 API 호출 시 일반 Exception이 발생하면 BusinessException을 던진다.")
    void fetchHolidays_GeneralException() {
        // given
        int year = 2025;
        String countryCode = "US";

        given(nagerApiProperties.publicHolidaysPath()).willReturn("/api/v3/PublicHolidays/{year}/{countryCode}");
        given(webClient.get()).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.uri("/api/v3/PublicHolidays/2025/US", year, countryCode)).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.bodyToFlux(Holiday.class))
                .willReturn(Flux.error(new RuntimeException("Network error")));

        // when & then
        assertThatThrownBy(() -> holidayWebClient.fetchHolidays(year, countryCode).block())
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("빈 공휴일 목록을 반환한다.")
    void fetchHolidays_EmptyList() {
        // given
        int year = 2025;
        String countryCode = "XX";

        given(nagerApiProperties.publicHolidaysPath()).willReturn("/api/v3/PublicHolidays/{year}/{countryCode}");
        given(webClient.get()).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.uri("/api/v3/PublicHolidays/2025/XX", year, countryCode)).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.bodyToFlux(Holiday.class)).willReturn(Flux.empty());

        // when
        Mono<List<Holiday>> result = holidayWebClient.fetchHolidays(year, countryCode);

        // then
        List<Holiday> actualHolidays = result.block();
        assertThat(actualHolidays).isEmpty();
    }

    @Test
    @DisplayName("단일 공휴일만 반환한다.")
    void fetchHolidays_SingleHoliday() {
        // given
        Country country = fixtureMonkey.giveMeBuilder(Country.class)
                .set("countryCode", "DE")
                .set("name", "Germany")
                .sample();

        Holiday holiday = fixtureMonkey.giveMeBuilder(Holiday.class)
                .set("name", "독일 통일의 날")
                .set("localName", "German Unity Day")
                .set("country", country)
                .set("date", LocalDate.of(2025, 10, 3))
                .set("fixed", true)
                .set("global", false)
                .set("launchYear", "1990")
                .sample();

        int year = 2025;
        String countryCode = "DE";

        given(nagerApiProperties.publicHolidaysPath()).willReturn("/api/v3/PublicHolidays/{year}/{countryCode}");
        given(webClient.get()).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.uri("/api/v3/PublicHolidays/2025/DE", year, countryCode)).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.bodyToFlux(Holiday.class)).willReturn(Flux.just(holiday));

        // when
        Mono<List<Holiday>> result = holidayWebClient.fetchHolidays(year, countryCode);

        // then
        List<Holiday> actualHolidays = result.block();
        assertThat(actualHolidays).containsExactly(holiday);
    }

    @Test
    @DisplayName("다른 연도와 국가 코드로 공휴일을 조회한다.")
    void fetchHolidays_DifferentYearAndCountry() {
        // given
        Country country = fixtureMonkey.giveMeBuilder(Country.class)
                .set("countryCode", "FR")
                .set("name", "France")
                .sample();

        Holiday holiday = fixtureMonkey.giveMeBuilder(Holiday.class)
                .set("name", "바스티유의 날")
                .set("localName", "Bastille Day")
                .set("country", country)
                .set("date", LocalDate.of(2024, 7, 14))
                .set("fixed", true)
                .set("global", false)
                .set("launchYear", "1880")
                .sample();

        int year = 2024;
        String countryCode = "FR";

        given(nagerApiProperties.publicHolidaysPath()).willReturn("/api/v3/PublicHolidays/{year}/{countryCode}");
        given(webClient.get()).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.uri("/api/v3/PublicHolidays/2024/FR", year, countryCode)).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.bodyToFlux(Holiday.class)).willReturn(Flux.just(holiday));

        // when
        Mono<List<Holiday>> result = holidayWebClient.fetchHolidays(year, countryCode);

        // then
        List<Holiday> actualHolidays = result.block();
        assertThat(actualHolidays).containsExactly(holiday);
    }
}