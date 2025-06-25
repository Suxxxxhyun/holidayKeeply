package com.holidaykeeply.domain.provider.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.holidaykeeply.domain.entity.Country;
import com.holidaykeeply.domain.infrastructure.repository.CountryRepository;
import com.holidaykeeply.domain.provider.CountryProvider;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Qualifier("countryDbProvider")
@Component
@RequiredArgsConstructor
public class CountryDbProvider implements CountryProvider {
  private final CountryRepository countryRepository;

  @Transactional(readOnly = true)
  public Mono<List<Country>> getCountries() {
    return Mono.fromCallable(countryRepository::findAll);
  }
}
