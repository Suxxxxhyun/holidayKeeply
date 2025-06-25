package com.holidaykeeply.domain.infrastructure.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.holidaykeeply.domain.entity.Country;
import com.holidaykeeply.domain.entity.Holiday;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long>, HolidayCustomRepository {
	void deleteByDateAndCountry(final LocalDate date, final Country country);

}
