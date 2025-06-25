package com.holidaykeeply.global.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.holidaykeeply.global.common.key.NagerApiProperties;

@Configuration
@EnableConfigurationProperties(NagerApiProperties.class)
public class AppConfig {
}

