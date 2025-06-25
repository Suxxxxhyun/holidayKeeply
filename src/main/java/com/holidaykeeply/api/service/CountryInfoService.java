package com.holidaykeeply.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.holidaykeeply.domain.entity.Country;
import com.holidaykeeply.domain.provider.CountryProvider;
import com.holidaykeeply.domain.service.CountryService;

import reactor.core.publisher.Mono;

@Service
public class CountryInfoService {
	private final CountryProvider countryProvider;
	private final CountryService countryService;

	public CountryInfoService(
			@Qualifier("countryApiProvider") CountryProvider countryProvider,
			CountryService countryService) {
		this.countryProvider = countryProvider;
		this.countryService = countryService;
	}

	public Mono<List<Country>> initializeCountries() {
		return countryProvider.getCountries()
				.flatMap(countryService::saveAll);
	}
}
