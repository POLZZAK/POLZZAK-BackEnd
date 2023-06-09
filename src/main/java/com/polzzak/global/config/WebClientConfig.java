package com.polzzak.global.config;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {
	@Bean
	public WebClient webClient() {
		return WebClient.builder()
			.clientConnector(new ReactorClientHttpConnector(httpClient()))
			.build();
	}

	private HttpClient httpClient() {
		return HttpClient.create()
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
			.responseTimeout(Duration.ofMillis(5000))
			.doOnConnected((connection) -> {
				connection
					.addHandlerFirst(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
					.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
			});
	}
}
