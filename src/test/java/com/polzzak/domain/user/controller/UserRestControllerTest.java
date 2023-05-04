package com.polzzak.domain.user.controller;

import static com.polzzak.global.common.HeadersConstant.*;
import static com.polzzak.support.TokenFixtures.*;
import static com.polzzak.support.UserFixtures.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import com.polzzak.domain.user.dto.LoginRequest;
import com.polzzak.domain.user.dto.RegisterRequest;
import com.polzzak.domain.user.dto.UserDto;
import com.polzzak.domain.user.entity.MemberType;
import com.polzzak.domain.user.entity.SocialType;
import com.polzzak.domain.user.service.UserAuthenticationService;
import com.polzzak.domain.user.service.UserService;
import com.polzzak.global.exception.ErrorCode;
import com.polzzak.global.exception.PolzzakException;
import com.polzzak.support.test.ControllerTestHelper;

@WebMvcTest(UserRestController.class)
class UserRestControllerTest extends ControllerTestHelper {

	@MockBean
	UserService userService;

	@MockBean
	UserAuthenticationService userAuthenticationService;

	@Test
	void 로그인_성공() throws Exception {
		// given
		String redirectUri = "redirectUri";
		String authenticationCode = "authenticationCode";
		LoginRequest loginRequest = new LoginRequest(authenticationCode, redirectUri);
		String username = TEST_USERNAME;
		String nickname = TEST_NICKNAME;
		String profileUrl = TEST_PROFILE_URL;
		UserDto userDto = new UserDto(username, nickname, MemberType.ETC, SocialType.KAKAO, profileUrl);
		String social = "kakao";

		// when
		when(userAuthenticationService.getSocialUsername(loginRequest, social)).thenReturn(username);
		when(userAuthenticationService.generateAccessTokenByUsername(username)).thenReturn(ACCESS_TOKEN);
		when(userAuthenticationService.generateRefreshTokenByUsername(username)).thenReturn(REFRESH_TOKEN);
		when(userService.getUserInfo(username)).thenReturn(userDto);

		// then
		mockMvc.perform(
				post("/api/v1/users/login/{social}", "kakao")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectToString(loginRequest))
			)
			.andExpectAll(status().isOk(), cookie().httpOnly(REFRESH_TOKEN_HEADER, true))
			.andDo(
				document(
					"{class-name}/user-login-success",
					pathParameters(parameterWithName("social").description("소셜 로그인 타입")),
					requestFields(
						fieldWithPath("authenticationCode").description("인가 코드"),
						fieldWithPath("redirectUri").description("redirectUri")
					),
					responseFields(
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("messages").description("응답 메시지"),
						fieldWithPath("data.accessToken").description("엑세스 토큰")
					)
				)
			);
	}

	@Test
	void 로그인_실패_회원가입_필요() throws Exception {
		// given
		String redirectUri = "redirectUri";
		String authenticationCode = "authenticationCode";
		LoginRequest loginRequest = new LoginRequest(authenticationCode, redirectUri);
		String username = TEST_USERNAME;
		String social = "kakao";

		// when
		when(userAuthenticationService.getSocialUsername(loginRequest, social)).thenReturn(username);
		doThrow(new IllegalArgumentException("사용자가 존재하지 않습니다")).when(userAuthenticationService)
			.validateUserByUsername(username);

		// then
		mockMvc.perform(
				post("/api/v1/users/login/{social}", "kakao")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectToString(loginRequest))
			)
			.andExpectAll(status().isBadRequest())
			.andDo(
				document(
					"{class-name}/user-login-fail-register",
					pathParameters(parameterWithName("social").description("소셜 로그인 타입")),
					requestFields(
						fieldWithPath("authenticationCode").description("인가 코드"),
						fieldWithPath("redirectUri").description("redirectUri")
					),
					responseFields(
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("messages").description("응답 메시지"),
						fieldWithPath("data.username").description("소셜 사용자 정보"),
						fieldWithPath("data.socialType").description("소셜 타입")
					)
				)
			);
	}

	@Test
	void 로그인_실패_소셜_로그인_실패() throws Exception {
		// given
		String redirectUri = "redirectUri";
		String authenticationCode = "authenticationCode";
		LoginRequest loginRequest = new LoginRequest(authenticationCode, redirectUri);
		String social = "kakao";

		// when
		when(userAuthenticationService.getSocialUsername(loginRequest, social)).thenThrow(
			new PolzzakException(ErrorCode.OAUTH_AUTHENTICATION_FAIL));

		// then
		mockMvc.perform(
				post("/api/v1/users/login/{social}", "kakao")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectToString(loginRequest))
			)
			.andExpectAll(status().isUnauthorized())
			.andDo(
				document(
					"{class-name}/user-login-fail-invalid",
					pathParameters(parameterWithName("social").description("소셜 로그인 타입")),
					requestFields(
						fieldWithPath("authenticationCode").description("인가 코드"),
						fieldWithPath("redirectUri").description("redirectUri")
					),
					responseFields(
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("messages").description("응답 메시지"),
						fieldWithPath("data").description("응답 데이터")
					)
				)
			);
	}

	@Test
	void 회원가입_성공_프로필_없는_경우() throws Exception {
		// given
		String username = TEST_USERNAME;
		String nickname = TEST_NICKNAME;
		RegisterRequest registerRequest = new RegisterRequest(username, SocialType.KAKAO, MemberType.ETC, nickname);
		String requestString = objectToString(registerRequest);
		MockMultipartFile requestPart =
			new MockMultipartFile("registerRequest", "registerRequest", "application/json", requestString.getBytes());
		String defaultProfileUrl = TEST_DEFAULT_PROFILE_URL;
		UserDto userDto = new UserDto(username, nickname, MemberType.ETC, SocialType.KAKAO, defaultProfileUrl);

		// when
		when(userAuthenticationService.register(registerRequest, null)).thenReturn(username);
		when(userAuthenticationService.generateAccessTokenByUsername(username)).thenReturn(ACCESS_TOKEN);
		when(userAuthenticationService.generateRefreshTokenByUsername(username)).thenReturn(REFRESH_TOKEN);

		// then
		mockMvc.perform(
				multipart("/api/v1/users/register")
					.file(requestPart)
					.contentType(MediaType.MULTIPART_FORM_DATA)
			)
			.andExpectAll(status().isOk(), cookie().httpOnly(REFRESH_TOKEN_HEADER, true))
			.andDo(
				document(
					"{class-name}/user-register-success-empty-profile",
					requestParts(
						partWithName("registerRequest").description(
							"사용자 정보 (json)\n" +
								"username: 소셜 사용자 정보\n" +
								"memberType: 사용자 타입\n" +
								"socialType: 소셜 타입\n" +
								"nickname: 닉네임\n"
						)
					),
					responseFields(
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("messages").description("응답 메시지"),
						fieldWithPath("data.accessToken").description("엑세스 토큰")
					)
				)
			);
	}

	@Test
	void 회원가입_성공_프로필_있는_경우() throws Exception {
		// given
		String username = TEST_USERNAME;
		String nickname = TEST_NICKNAME;
		RegisterRequest registerRequest = new RegisterRequest(username, SocialType.KAKAO, MemberType.ETC, nickname);
		String requestString = objectToString(registerRequest);
		MockMultipartFile requestPart =
			new MockMultipartFile("registerRequest", "registerRequest", "application/json", requestString.getBytes());
		MockMultipartFile profile = TEST_PROFILE;
		String profileUrl = TEST_PROFILE_URL;
		UserDto userDto = new UserDto(username, nickname, MemberType.ETC, SocialType.KAKAO, profileUrl);

		// when
		when(userAuthenticationService.register(registerRequest, profile)).thenReturn(username);
		when(userAuthenticationService.generateAccessTokenByUsername(username)).thenReturn(ACCESS_TOKEN);
		when(userAuthenticationService.generateRefreshTokenByUsername(username)).thenReturn(REFRESH_TOKEN);

		// then
		mockMvc.perform(
				multipart("/api/v1/users/register")
					.file(requestPart)
					.file(profile)
					.contentType(MediaType.MULTIPART_FORM_DATA)
			)
			.andExpectAll(status().isOk(), cookie().httpOnly(REFRESH_TOKEN_HEADER, true))
			.andDo(
				document(
					"{class-name}/user-register-success-profile",
					requestParts(
						partWithName("registerRequest").description(
							"사용자 정보 (json)\n" +
								"username: 소셜 사용자 정보\n" +
								"memberType: 사용자 타입\n" +
								"socialType: 소셜 타입\n" +
								"nickname: 닉네임\n"
						),
						partWithName("profile").description("사용자 프로필")
					),
					responseFields(
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("messages").description("응답 메시지"),
						fieldWithPath("data.accessToken").description("엑세스 토큰")
					)
				)
			);
	}

	@Test
	void 회원가입_실패_닉네임_중복() throws Exception {
		// given
		String username = TEST_USERNAME;
		String nickname = TEST_NICKNAME;
		RegisterRequest registerRequest = new RegisterRequest(username, SocialType.KAKAO, MemberType.ETC, nickname);
		String requestString = objectToString(registerRequest);
		MockMultipartFile requestPart =
			new MockMultipartFile("registerRequest", "registerRequest", "application/json", requestString.getBytes());
		MockMultipartFile profile = TEST_PROFILE;
		String profileUrl = TEST_PROFILE_URL;
		UserDto userDto = new UserDto(username, nickname, MemberType.ETC, SocialType.KAKAO, profileUrl);

		// when
		when(userAuthenticationService.register(registerRequest, profile)).thenThrow(
			new IllegalArgumentException("이미 존재하는 사용자입니다"));

		// then
		mockMvc.perform(
				multipart("/api/v1/users/register")
					.file(requestPart)
					.file(profile)
					.contentType(MediaType.MULTIPART_FORM_DATA)
			)
			.andExpectAll(status().isBadRequest())
			.andDo(
				document(
					"{class-name}/user-register-fail",
					requestParts(
						partWithName("registerRequest").description(
							"사용자 정보 (json)\n" +
								"username: 소셜 사용자 정보\n" +
								"memberType: 사용자 타입\n" +
								"socialType: 소셜 타입\n" +
								"nickname: 중복된 닉네임\n"
						),
						partWithName("profile").description("사용자 프로필")
					),
					responseFields(
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("messages").description("응답 메시지"),
						fieldWithPath("data").description("응답 데이터")
					)
				)
			);
	}

	@Test
	void 닉네임_중복_없음() throws Exception {
		// given
		String nickname = TEST_NICKNAME;

		// when
		doNothing().when(userAuthenticationService).validateNickname(nickname);

		// then
		mockMvc.perform(
				get("/api/v1/users/validate/nickname")
					.param("value", nickname)
			)
			.andExpectAll(status().isNoContent())
			.andDo(
				document(
					"{class-name}/user-valid-nickname-success",
					queryParameters(parameterWithName("value").description("닉네임"))
				)
			);
	}

	@Test
	void 닉네임_중복_발생() throws Exception {
		// given
		String nickname = TEST_NICKNAME;

		// when
		doThrow(new IllegalArgumentException("이미 존재하는 사용자입니다")).when(userAuthenticationService)
			.validateNickname(nickname);

		// then
		mockMvc.perform(
				get("/api/v1/users/validate/nickname")
					.param("value", nickname)
			)
			.andExpectAll(status().isBadRequest())
			.andDo(
				document(
					"{class-name}/user-valid-nickname-duplication",
					queryParameters(parameterWithName("value").description("중복된 닉네임")),
					responseFields(
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("messages").description("응답 메시지"),
						fieldWithPath("data").description("응답 데이터")
					)
				)
			);
	}

	@Test
	void 사용자_정보_조회_성공() throws Exception {
		// given
		String accessToken = ACCESS_TOKEN;
		String username = TEST_USERNAME;
		UserDto userDto = new UserDto(username, TEST_NICKNAME, MemberType.ETC, SocialType.KAKAO,
			TEST_PROFILE_URL);

		// when
		when(userService.getUserInfo(username)).thenReturn(userDto);

		// then
		mockMvc.perform(
				get("/api/v1/users/me")
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + accessToken)
			)
			.andExpectAll(status().isOk())
			.andDo(
				document(
					"{class-name}/user-get-info-success",
					requestHeaders(
						headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
					),
					responseFields(
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("messages").description("응답 메시지"),
						fieldWithPath("data.nickname").description("닉네임"),
						fieldWithPath("data.memberType").description("사용자 타입"),
						fieldWithPath("data.profileUrl").description("프로필 Url")
					)
				)
			);
	}
}
