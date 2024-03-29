package com.polzzak.domain.user.service;

import java.util.Locale;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.polzzak.domain.memberpoint.service.MemberPointService;
import com.polzzak.domain.membertype.entity.MemberTypeDetail;
import com.polzzak.domain.membertype.service.MemberTypeDetailService;
import com.polzzak.domain.notification.service.NotificationService;
import com.polzzak.domain.user.dto.LoginRequest;
import com.polzzak.domain.user.dto.OAuthUserInfoResponse;
import com.polzzak.domain.user.dto.RegisterRequest;
import com.polzzak.domain.user.dto.UserDto;
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
	private final MemberPointService memberPointService;
	private final TokenProvider tokenProvider;
	private final KakaoOAuthProperties kakaoOAuthProperties;
	private final GoogleOAuthProperties googleOAuthProperties;
	private final NotificationService notificationService;

	public AuthenticationService(final UserRepository userRepository, final WebClient webClient,
		final FileClient fileClient, final MemberTypeDetailService memberTypeDetailService,
		final MemberPointService memberPointService, final TokenProvider tokenProvider,
		final KakaoOAuthProperties kakaoOAuthProperties, final GoogleOAuthProperties googleOAuthProperties,
		final NotificationService notificationService) {
		this.userRepository = userRepository;
		this.memberPointService = memberPointService;
		this.webClient = webClient;
		this.fileClient = fileClient;
		this.memberTypeDetailService = memberTypeDetailService;
		this.tokenProvider = tokenProvider;
		this.kakaoOAuthProperties = kakaoOAuthProperties;
		this.googleOAuthProperties = googleOAuthProperties;
		this.notificationService = notificationService;
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
		MemberTypeDetail findMemberTypeDetail = memberTypeDetailService.findMemberTypeDetailById(
			registerRequest.memberTypeDetailId());
		Member member = createMember(registerRequest, findMemberTypeDetail);

		String profileKey = null;
		if (profile != null) {
			profileKey = fileClient.uploadFile(profile, FileType.PROFILE_IMAGE);
		}
		member.changeProfileKey(profileKey);
		User saveUser = userRepository.save(createUser(registerRequest, member));
		memberPointService.saveMemberPoint(saveUser.getMember());
		notificationService.createNotificationSetting(saveUser.getMember().getId());
		return new TokenPayload(saveUser.getId().toString(), saveUser.getUsername(), saveUser.getUserRole().toString());
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

	public UserDto getUserByUsername(String username) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다"));
		return UserDto.from(user);
	}

	public long getRefreshExpiredTimeSec() {
		return tokenProvider.getRefreshExpiredTimeSec();
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

	private User createUser(final RegisterRequest registerRequest, final Member member) {
		return User.createUser()
			.username(registerRequest.username())
			.socialType(registerRequest.socialType())
			.userRole(UserRole.ROLE_USER)
			.member(member)
			.build();
	}

	private Member createMember(final RegisterRequest registerRequest, MemberTypeDetail findMemberTypeDetail) {
		return Member.createMember()
			.nickname(registerRequest.nickname())
			.memberType(findMemberTypeDetail)
			.build();
	}
}
