package com.polzzak.domain.user.service;

import java.util.Locale;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.polzzak.domain.membertype.entity.MemberTypeDetail;
import com.polzzak.domain.membertype.service.MemberTypeDetailService;
import com.polzzak.domain.user.dto.LoginRequest;
import com.polzzak.domain.user.dto.OAuthUserInfoResponse;
import com.polzzak.domain.user.dto.RegisterRequest;
import com.polzzak.domain.user.entity.Member;
import com.polzzak.domain.user.entity.SocialType;
import com.polzzak.domain.user.entity.User;
import com.polzzak.domain.user.entity.UserRole;
import com.polzzak.domain.user.properties.GoogleOAuthProperties;
import com.polzzak.domain.user.properties.KakaoOAuthProperties;
import com.polzzak.domain.user.properties.OAuthProperties;
import com.polzzak.domain.user.repository.UserRepository;
import com.polzzak.global.common.FileType;
import com.polzzak.global.exception.ErrorCode;
import com.polzzak.global.exception.PolzzakException;
import com.polzzak.global.infra.file.FileClient;
import com.polzzak.global.security.TokenPayload;
import com.polzzak.global.security.TokenProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
public class AuthenticationService {
	private final UserRepository userRepository;

	private final WebClient webClient;
	private final FileClient fileClient;
	private final MemberTypeDetailService memberTypeDetailService;
	private final TokenProvider tokenProvider;
	private final KakaoOAuthProperties kakaoOAuthProperties;
	private final GoogleOAuthProperties googleOAuthProperties;

	private final String defaultProfileKey = "profile/default_profile.png";

	public AuthenticationService(final UserRepository userRepository, final WebClient webClient,
		final FileClient fileClient, final MemberTypeDetailService memberTypeDetailService,
		final TokenProvider tokenProvider,
		final KakaoOAuthProperties kakaoOAuthProperties, final GoogleOAuthProperties googleOAuthProperties) {
		this.userRepository = userRepository;
		this.webClient = webClient;
		this.fileClient = fileClient;
		this.memberTypeDetailService = memberTypeDetailService;
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
	public TokenPayload register(final RegisterRequest registerRequest, final MultipartFile profile) {
		validateNickname(registerRequest.nickname());

		String fileKey = null;
		if (profile != null) {
			fileKey = fileClient.uploadFile(profile, FileType.PROFILE_IMAGE);
		}

		User saveUser = userRepository.save(
			createUser(registerRequest, fileKey == null ? Optional.empty() : Optional.of(fileKey)));
		return new TokenPayload(saveUser.getUsername(), saveUser.getUserRole().toString());
	}

	public String generateAccessToken(final TokenPayload tokenPayload) {
		return tokenProvider.createAccessToken(tokenPayload);
	}

	public String generateRefreshToken(final TokenPayload tokenPayload) {
		return tokenProvider.createRefreshToken(tokenPayload);
	}

	public void validateNickname(final String nickname) {
		if (!userRepository.existsByNickname(nickname).isEmpty()) {
			throw new IllegalArgumentException("이미 존재하는 사용자입니다");
		}
	}

	public String getUserRoleByUsername(String username) {
		return userRepository.findByUsername(username)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다"))
			.getUserRole()
			.toString();
	}

	private String getSocialUserInfo(final LoginRequest loginRequest, final OAuthProperties oAuthProperties) {
		return getUserInfo(OAuthUserInfoResponse.class, oAuthProperties.getUserInfoUrl(),
			loginRequest.oAuthAccessToken()).id();
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
		MemberTypeDetail findMemberTypeDetail = memberTypeDetailService.findMemberTypeDetailById(
			registerRequest.memberTypeDetailId());

		Member member = Member.createMember()
			.nickname(registerRequest.nickname())
			.memberType(findMemberTypeDetail)
			.profileKey(profileKey.orElse(defaultProfileKey))
			.build();

		return User.createUser()
			.username(registerRequest.username())
			.socialType(registerRequest.socialType())
			.userRole(UserRole.ROLE_USER)
			.member(member)
			.build();
	}
}
