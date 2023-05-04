package com.polzzak.global.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.polzzak.global.auth.LoginUsernameResolver;

@Configuration
public class AuthenticationConfig implements WebMvcConfigurer {
	private final LoginUsernameResolver loginUsernameResolver;

	public AuthenticationConfig(final LoginUsernameResolver loginUsernameResolver) {
		this.loginUsernameResolver = loginUsernameResolver;
	}

	@Override
	public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(loginUsernameResolver);
	}
}
