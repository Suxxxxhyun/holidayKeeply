package com.holidaykeeply.domain.provider;

import java.util.List;

import com.holidaykeeply.domain.entity.Country;

import reactor.core.publisher.Mono;

public interface CountryProvider {
  public Mono<List<Country>> getCountries();
}
