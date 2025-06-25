package com.holidaykeeply.domain.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.holidaykeeply.domain.entity.Country;

public interface CountryRepository extends JpaRepository<Country, Long> {
	Optional<Country> findByCountryCode(final String name);
	boolean existsByCountryCode(final String name);
	boolean existsByName(final String name);
}
