package com.polzzak.support.test;

import static com.polzzak.support.TokenFixtures.*;
import static com.polzzak.support.UserFixtures.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.polzzak.global.exception.ErrorCode;
import com.polzzak.global.exception.JwtException;
import com.polzzak.global.security.LoginUsername;
import com.polzzak.global.security.LoginUsernameResolver;
import com.polzzak.global.security.TokenProvider;

@TestEnvironment
@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
public abstract class ControllerTestHelper {
	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@MockBean
	private TokenProvider tokenProvider;

	@MockBean
	private LoginUsernameResolver loginUsernameResolver;

	@BeforeEach
	protected void setUp(final WebApplicationContext webApplicationContext,
		final RestDocumentationContextProvider restDocumentationContextProvider) throws Exception {
		objectMapper.registerModule(new JavaTimeModule());

		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.addFilter(new CharacterEncodingFilter("UTF-8", true))
			.alwaysDo(MockMvcResultHandlers.print())
			.apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentationContextProvider)
				.operationPreprocessors()
				.withRequestDefaults(Preprocessors.prettyPrint())
				.withResponseDefaults(Preprocessors.prettyPrint())
			)
			.build();

		when(loginUsernameResolver.supportsParameter(any())).thenAnswer(invocation -> {
			MethodParameter methodParameter = invocation.getArgument(0);
			return methodParameter.hasParameterAnnotation(LoginUsername.class);
		});

		when(loginUsernameResolver.resolveArgument(any(), any(), any(), any())).thenAnswer(invocation -> {
			NativeWebRequest webRequest = invocation.getArgument(2);

			checkAccessToken(webRequest);

			return TEST_USERNAME;
		});

		when(tokenProvider.getSubject(REFRESH_TOKEN)).thenReturn(TEST_USERNAME);
		when(tokenProvider.isValidToken(REFRESH_TOKEN)).thenReturn(true);
		when(tokenProvider.isValidToken(INVALID_REFRESH_TOKEN)).thenReturn(false);
	}

	protected String objectToString(final Object data) {
		try {
			return objectMapper.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private void checkAccessToken(final NativeWebRequest webRequest) {
		String accessToken = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
		if (accessToken == null) {
			throw new JwtException(ErrorCode.ACCESS_TOKEN_INVALID);
		}

		String[] tokenFormat = accessToken.split(" ");
		if (!tokenFormat[0].equals(TOKEN_TYPE.trim()) || tokenFormat[1].equals(INVALID_ACCESS_TOKEN)) {
			throw new JwtException(ErrorCode.ACCESS_TOKEN_INVALID);
		}

		if (tokenFormat[1].equals(EXPIRED_ACCESS_TOKEN)) {
			throw new JwtException(ErrorCode.ACCESS_TOKEN_EXPIRED);
		}
	}
}
