package com.polzzak.global.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class AsyncConfig {
	private final int corePoolSize = 3;
	private final int maxPoolSize = 10;
	private final int queueCapacity = 10;
	private final String customThreadNamePrefix = "ASYNC_THREAD-";

	@Bean
	public Executor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(corePoolSize);
		taskExecutor.setMaxPoolSize(maxPoolSize);
		taskExecutor.setQueueCapacity(queueCapacity);
		taskExecutor.setThreadNamePrefix(customThreadNamePrefix);
		taskExecutor.initialize();
		return taskExecutor;
	}
}
