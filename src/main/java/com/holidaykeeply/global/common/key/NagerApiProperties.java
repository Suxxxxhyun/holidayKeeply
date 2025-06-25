package com.holidaykeeply.global.common.key;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nager.api")
public record NagerApiProperties(
	String baseUrl,
	String availableCountriesPath,
	String publicHolidaysPath
) {}
