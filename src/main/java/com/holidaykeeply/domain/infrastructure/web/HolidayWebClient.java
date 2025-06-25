package com.holidaykeeply.domain.infrastructure.web;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.holidaykeeply.domain.entity.Holiday;
import com.holidaykeeply.global.common.key.NagerApiProperties;
import com.holidaykeeply.global.error.ErrorCode;
import com.holidaykeeply.global.error.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class HolidayWebClient {
	private final WebClient webClient;
	private final NagerApiProperties nagerApiProperties;

	public Mono<List<Holiday>> fetchHolidays(final int year, final String countryCode) {

		String url = nagerApiProperties.publicHolidaysPath()
			.replace("{year}", String.valueOf(year))
			.replace("{countryCode}", countryCode);

		return webClient.get()
			.uri(url, year, countryCode)
			.retrieve()
			.bodyToFlux(Holiday.class)
			.collectList()
			.onErrorMap(WebClientResponseException.class,
				ex -> new BusinessException(ErrorCode.EXTERNAL_API_ERROR))
			.onErrorMap(Exception.class,
				ex -> new BusinessException(ErrorCode.EXTERNAL_API_ERROR));
	}
}
