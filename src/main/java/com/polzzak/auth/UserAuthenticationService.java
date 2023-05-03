package com.polzzak.auth;

import com.polzzak.auth.model.*;
import com.polzzak.file.FileService;
import com.polzzak.common.model.ErrorCode;
import com.polzzak.common.model.PolzzakException;
import com.polzzak.member.model.Member;
import com.polzzak.user.model.*;
import com.polzzak.user.UserRepository;
import com.polzzak.file.model.FileType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserAuthenticationService {
    private final UserRepository userRepository;

    private final WebClient webClient;
    private final FileService fileService;
    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoOAuthProperties kakaoOAuthProperties;
    private final GoogleOAuthProperties googleOAuthProperties;

    private final String DEFAULT_PROFILE_KEY = "profile/default_profile.png";

    public UserAuthenticationService(final UserRepository userRepository, final WebClient webClient,
                                     final FileService fileService, final JwtTokenProvider jwtTokenProvider,
                                     final KakaoOAuthProperties kakaoOAuthProperties, final GoogleOAuthProperties googleOAuthProperties) {
        this.userRepository = userRepository;
        this.webClient = webClient;
        this.fileService = fileService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.kakaoOAuthProperties = kakaoOAuthProperties;
        this.googleOAuthProperties = googleOAuthProperties;
    }

    public String getKakaoUserInfo(final LoginRequest loginRequest) {
        MultiValueMap<String, String> params = getTokenParams(loginRequest, kakaoOAuthProperties.getApiKey(), kakaoOAuthProperties.getSecretKey());
        String accessToken = getOAuthAccessToken(kakaoOAuthProperties.getKakaoTokenUrl(), params).accessToken();
        String id = getUserInfo(OAuthUserInfoResponse.class, kakaoOAuthProperties.getKakaoUserInfoUrl(), accessToken).id();
        return getUsername(id, SocialType.KAKAO);
    }

    public String getGoogleUserInfo(final LoginRequest loginRequest) {
        MultiValueMap<String, String> params = getTokenParams(loginRequest, googleOAuthProperties.getApiKey(), googleOAuthProperties.getSecretKey());
        String accessToken = getOAuthAccessToken(googleOAuthProperties.getGoogleTokenUrl(), params).accessToken();
        String id = getUserInfo(OAuthUserInfoResponse.class, googleOAuthProperties.getGoogleUserInfoUrl(), accessToken).id();
        return getUsername(id, SocialType.GOOGLE);
    }

    @Transactional
    public UserDto register(final RegisterRequest registerRequest, final MultipartFile profile) {
        validateNickname(registerRequest.nickname());

        String fileKey = null;
        if (profile != null) {
            fileKey = fileService.uploadFile(profile, FileType.PROFILE_IMAGE);
        }

        User saveUser = userRepository.save(createUser(registerRequest, fileKey == null ? Optional.empty() : Optional.of(fileKey)));
        return UserDto.from(saveUser, null);
    }

    public String generateAccessTokenByUsername(final String username) {
        return jwtTokenProvider.createAccessToken(username);
    }

    public String generateRefreshTokenByUsername(final String username) {
        return jwtTokenProvider.createRefreshToken(username);
    }

    public void validateNickname(final String nickname) {
        if (!userRepository.existsByNickname(nickname).isEmpty()) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다");
        }
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

    private MultiValueMap<String, String> getTokenParams(final LoginRequest loginRequest, final String apiKey, final String secretKey) {
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

    private String getUsername(String id, SocialType socialType) {
        return socialType.name() + "_" + id;
    }

    private User createUser(final RegisterRequest registerRequest, final Optional<String> profileKey) {
        Member member = Member.createMember()
            .nickname(registerRequest.nickname())
            .memberType(registerRequest.memberType())
            .profileKey(profileKey.orElse(DEFAULT_PROFILE_KEY))
            .build();

        return User.createUser()
            .username(registerRequest.username())
            .socialType(registerRequest.socialType())
            .member(member)
            .build();
    }
}
