package com.holidaykeeply.domain.infrastructure.web;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.holidaykeeply.domain.entity.Country;
import com.holidaykeeply.global.common.key.NagerApiProperties;
import com.holidaykeeply.global.error.ErrorCode;
import com.holidaykeeply.global.error.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CountryWebClient {

	private final WebClient webClient;
	private final NagerApiProperties nagerApiProperties;

	public Mono<List<Country>> fetchCountries() {
		return webClient.get()
				.uri(nagerApiProperties.availableCountriesPath())
				.retrieve()
				.bodyToFlux(Country.class)
				.collectList()
				.onErrorMap(WebClientResponseException.class,
				ex -> new BusinessException(ErrorCode.EXTERNAL_API_ERROR))
				.onErrorMap(Exception.class,
				ex -> new BusinessException(ErrorCode.EXTERNAL_API_ERROR));
	}
}
