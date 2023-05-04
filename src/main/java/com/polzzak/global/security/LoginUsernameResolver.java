package com.polzzak.global.security;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginUsernameResolver implements HandlerMethodArgumentResolver {

	private final TokenProvider tokenProvider;

	public LoginUsernameResolver(final TokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}

	@Override
	public boolean supportsParameter(final MethodParameter parameter) {
		return parameter.hasParameterAnnotation(LoginUsername.class) && parameter.getParameterType()
			.isAssignableFrom(String.class);
	}

	@Override
	public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer,
		final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) throws Exception {
		String accessToken = tokenProvider.extractAccessToken(webRequest);
		return tokenProvider.getSubject(accessToken);
	}
}
