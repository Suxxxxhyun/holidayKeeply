package com.holidaykeeply.global.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import com.holidaykeeply.global.common.key.NagerApiProperties;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import reactor.netty.http.client.HttpClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

	private final NagerApiProperties nagerApiProperties;

	@Bean
	public WebClient webClient(final WebClient.Builder builder) {
		HttpClient httpClient = HttpClient.create()
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
			.responseTimeout(Duration.ofSeconds(10))
			.doOnConnected(conn ->
				conn.addHandlerLast(new ReadTimeoutHandler(10))
					.addHandlerLast(new WriteTimeoutHandler(10))
			);

		return builder
			.baseUrl(nagerApiProperties.baseUrl())
			.clientConnector(new ReactorClientHttpConnector(httpClient))
			.defaultHeaders(httpHeaders -> {
				httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
			})
			.build();
	}
}
