package com.holidaykeeply.global.runner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.holidaykeeply.api.service.CountryInfoService;
import com.holidaykeeply.api.service.HolidayInfoService;
import com.holidaykeeply.domain.service.CountryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Profile("!test")
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final CountryService countryService;
    private final CountryInfoService countryInfoService;
    private final HolidayInfoService holidayInfoService;

    @Override
    public void run(ApplicationArguments args) {
        Long count = countryService.count().block();

        if (count != null && count == 0) {
            log.info("First run detected. Initializing data...");
            countryInfoService.initializeCountries()
                    .flatMap(holidayInfoService::initializeHolidays)
                    .doOnSuccess(v -> log.info("Data initialization finished successfully."))
                    .doOnError(e -> log.error("Data initialization failed.", e))
                    .onErrorResume(e -> Mono.empty()) // ★ 이 줄을 추가하세요!
                    .block();
        } else {
            log.info("Data already exists. Skipping initialization.");
        }
    }
} 