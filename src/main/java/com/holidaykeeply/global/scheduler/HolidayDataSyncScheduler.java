package com.holidaykeeply.global.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.holidaykeeply.api.service.HolidayInfoService;
import com.holidaykeeply.domain.provider.CountryProvider;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class HolidayDataSyncScheduler {

    private final HolidayInfoService holidayInfoService;
    private final CountryProvider countryProvider;

    public HolidayDataSyncScheduler(
            @Qualifier("countryDbProvider") CountryProvider countryProvider,
            HolidayInfoService holidayInfoService) {
        this.countryProvider = countryProvider;
        this.holidayInfoService = holidayInfoService;
    }

    /**
     * 매년 1월 2일 01:00 KST에 전년도와 금년도 공휴일 데이터를 동기화
     * cron: 초 분 시 일 월 요일 (KST 기준)
     */
    @Scheduled(cron = "0 0 1 2 1 *", zone = "Asia/Seoul")
    public void syncHolidayData() {
        log.info("Starting annual holiday data synchronization...");

        try {
            LocalDate currentYear = LocalDate.now();
            LocalDate previousYear = currentYear.minusYears(1);

            log.info("Syncing holiday data for years: {} and {}", previousYear.getYear(), currentYear.getYear());

            countryProvider.getCountries()
                    .flatMap(countries -> {
                        log.info("Found {} countries to sync", countries.size());
                        return holidayInfoService.syncHolidaysForYears(countries,
                                List.of(previousYear.getYear(), currentYear.getYear()));
                    })
                    .doOnSuccess(v -> log.info("Annual holiday data synchronization completed successfully"))
                    .doOnError(error -> log.error("Annual holiday data synchronization failed", error))
                    .onErrorResume(error -> {
                        log.error("Error during holiday data sync: {}", error.getMessage(), error);
                        return Mono.empty();
                    })
                    .subscribe();
        } catch (Exception e) {
            log.error("Unexpected error in syncHolidayData", e);
        }
    }
}