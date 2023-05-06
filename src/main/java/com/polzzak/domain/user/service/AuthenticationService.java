package com.polzzak.domain.user.service;

import java.util.Locale;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.polzzak.domain.user.dto.LoginRequest;
import com.polzzak.domain.user.dto.OAuthUserInfoResponse;
import com.polzzak.domain.user.dto.OauthAccessTokenDto;
import com.polzzak.domain.user.dto.RegisterRequest;
import com.polzzak.domain.user.entity.Member;
import com.polzzak.domain.user.entity.SocialType;
import com.polzzak.domain.user.entity.User;
import com.polzzak.domain.user.properties.GoogleOAuthProperties;
import com.polzzak.domain.user.properties.KakaoOAuthProperties;
import com.polzzak.domain.user.properties.OAuthProperties;
import com.polzzak.domain.user.repository.UserRepository;
import com.polzzak.global.common.FileType;
import com.polzzak.global.exception.ErrorCode;
import com.polzzak.global.exception.PolzzakException;
import com.polzzak.global.infra.file.FileClient;
import com.polzzak.global.security.TokenProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
public class AuthenticationService {
	private final UserRepository userRepository;

	private final WebClient webClient;
	private final FileClient fileClient;
	private final TokenProvider tokenProvider;
	private final KakaoOAuthProperties kakaoOAuthProperties;
	private final GoogleOAuthProperties googleOAuthProperties;

	private final String defaultProfileKey = "profile/default_profile.png";

	public AuthenticationService(final UserRepository userRepository, final WebClient webClient,
		final FileClient fileClient, final TokenProvider tokenProvider,
		final KakaoOAuthProperties kakaoOAuthProperties, final GoogleOAuthProperties googleOAuthProperties) {
		this.userRepository = userRepository;
		this.webClient = webClient;
		this.fileClient = fileClient;
		this.tokenProvider = tokenProvider;
		this.kakaoOAuthProperties = kakaoOAuthProperties;
		this.googleOAuthProperties = googleOAuthProperties;
	}

	public String getSocialUsername(final LoginRequest loginRequest, final String social) {
		SocialType socialType = SocialType.valueOf(social.toUpperCase(Locale.ROOT));

		return switch (socialType) {
			case KAKAO -> convertUserInfoToUsername(getSocialUserInfo(loginRequest, kakaoOAuthProperties), socialType);
			case GOOGLE ->
				convertUserInfoToUsername(getSocialUserInfo(loginRequest, googleOAuthProperties), socialType);
			case APPLE -> null;
		};
	}

	@Transactional
	public String register(final RegisterRequest registerRequest, final MultipartFile profile) {
		validateNickname(registerRequest.nickname());

		String fileKey = null;
		if (profile != null) {
			fileKey = fileClient.uploadFile(profile, FileType.PROFILE_IMAGE);
		}

		User saveUser = userRepository.save(
			createUser(registerRequest, fileKey == null ? Optional.empty() : Optional.of(fileKey)));
		return saveUser.getUsername();
	}

	public String generateAccessTokenByUsername(final String username) {
		return tokenProvider.createAccessToken(username);
	}

	public String generateRefreshTokenByUsername(final String username) {
		return tokenProvider.createRefreshToken(username);
	}

	public void validateNickname(final String nickname) {
		if (!userRepository.existsByNickname(nickname).isEmpty()) {
			throw new IllegalArgumentException("이미 존재하는 사용자입니다");
		}
	}

	public void validateUserByUsername(String username) {
		if (!userRepository.existsByUsername(username).isEmpty()) {
			throw new IllegalArgumentException("존재하지 않는 사용자입니다");
		}
	}

	private String getSocialUserInfo(final LoginRequest loginRequest, final OAuthProperties oAuthProperties) {
		MultiValueMap<String, String> params = getTokenParams(loginRequest, oAuthProperties.getApiKey(),
			oAuthProperties.getSecretKey());
		String accessToken = getOAuthAccessToken(oAuthProperties.getTokenUrl(), params).accessToken();
		return getUserInfo(OAuthUserInfoResponse.class, oAuthProperties.getUserInfoUrl(),
			accessToken).id();
	}

	private OauthAccessTokenDto getOAuthAccessToken(final String uri, final MultiValueMap<String, String> params) {
		return webClient.post()
			.uri(uri)
			.body(BodyInserters.fromFormData(params))
			.retrieve()
			.onStatus(
				httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError(),
				clientResponse -> clientResponse.bodyToMono(String.class).map((data) -> {
					log.error("[OAuth Exception] {}", data);
					return new PolzzakException(ErrorCode.OAUTH_AUTHENTICATION_FAIL);
				})
			)
			.bodyToMono(OauthAccessTokenDto.class)
			.block();
	}

	private MultiValueMap<String, String> getTokenParams(final LoginRequest loginRequest, final String apiKey,
		final String secretKey) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", apiKey);
		params.add("redirect_uri", loginRequest.redirectUri());
		params.add("code", loginRequest.authenticationCode());
		params.add("client_secret", secretKey);
		return params;
	}

	private <R> R getUserInfo(Class<R> responseClass, String uri, String token) {
		return webClient
			.get()
			.uri(uri)
			.header(HttpHeaders.AUTHORIZATION, getBearerToken(token))
			.retrieve()
			.onStatus(
				httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError(),
				clientResponse -> clientResponse.bodyToMono(String.class).map(data -> {
					log.error("[OAuth Exception] {}", data);
					return new PolzzakException(ErrorCode.OAUTH_AUTHENTICATION_FAIL);
				})
			)
			.bodyToMono(responseClass)
			.block();
	}

	private String getBearerToken(String token) {
		return "Bearer " + token;
	}

	private String convertUserInfoToUsername(String id, SocialType socialType) {
		return socialType.name() + "_" + id;
	}

	private User createUser(final RegisterRequest registerRequest, final Optional<String> profileKey) {
		Member member = Member.createMember()
			.nickname(registerRequest.nickname())
			.memberType(registerRequest.memberType())
			.profileKey(profileKey.orElse(defaultProfileKey))
			.build();

		return User.createUser()
			.username(registerRequest.username())
			.socialType(registerRequest.socialType())
			.member(member)
			.build();
	}
}
