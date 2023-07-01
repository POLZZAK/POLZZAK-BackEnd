package com.polzzak.global.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.polzzak.global.security.LoginIdResolver;
import com.polzzak.global.security.LoginUsernameResolver;

@Configuration
public class AuthenticationConfig implements WebMvcConfigurer {
	private final LoginUsernameResolver loginUsernameResolver;
	private final LoginIdResolver loginIdResolver;

	public AuthenticationConfig(final LoginUsernameResolver loginUsernameResolver,
		final LoginIdResolver loginIdResolver) {
		this.loginUsernameResolver = loginUsernameResolver;
		this.loginIdResolver = loginIdResolver;
	}

	@Override
	public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.addAll(List.of(loginUsernameResolver, loginIdResolver));
	}
}
