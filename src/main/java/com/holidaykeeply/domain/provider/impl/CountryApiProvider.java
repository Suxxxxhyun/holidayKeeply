package com.holidaykeeply.domain.provider.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.holidaykeeply.domain.entity.Country;
import com.holidaykeeply.domain.infrastructure.web.CountryWebClient;
import com.holidaykeeply.domain.provider.CountryProvider;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Qualifier("countryApiProvider")
@Component
@RequiredArgsConstructor
public class CountryApiProvider implements CountryProvider {
  private final CountryWebClient countryWebClient;

  public Mono<List<Country>> getCountries() {
    return countryWebClient.fetchCountries();
  }
}
