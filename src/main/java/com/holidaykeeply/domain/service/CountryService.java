package com.holidaykeeply.domain.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.holidaykeeply.domain.entity.Country;
import com.holidaykeeply.domain.infrastructure.repository.CountryRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@Transactional
@RequiredArgsConstructor
public class CountryService {
	private final CountryRepository countryRepository;

	@Transactional(readOnly = true)
	public Mono<Long> count() {
		return Mono.fromCallable(countryRepository::count);
	}

	@Transactional(readOnly = true)
	public Country findByCountryCode(String name) {
		return countryRepository.findByCountryCode(name).orElseThrow(NoSuchElementException::new);
	}

	public Mono<List<Country>> saveAll(List<Country> countries) {
		return Mono.fromCallable(() -> {
			return countryRepository.saveAll(countries);
		});
	}
}
