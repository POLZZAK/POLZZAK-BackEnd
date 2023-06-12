package com.polzzak.domain.user.controller;

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
import com.polzzak.domain.user.service.AuthenticationService;
import com.polzzak.global.exception.ErrorCode;
import com.polzzak.global.exception.PolzzakException;
import com.polzzak.support.test.ControllerTestHelper;

@WebMvcTest(AuthRestController.class)
class AuthRestControllerTest extends ControllerTestHelper {

	@MockBean
	AuthenticationService authenticationService;

	@Test
	void 로그인_성공() throws Exception {
		// given
		LoginRequest loginRequest = new LoginRequest(TEST_OAUTH_ACCESS_TOKEN);

		// when
		when(authenticationService.getSocialUsername(loginRequest, TEST_SOCIAL_TYPE)).thenReturn(TEST_USERNAME);
		when(authenticationService.getUserRoleByUsername(TEST_USERNAME)).thenReturn(TEST_USER_ROLE);
		doNothing().when(authenticationService).validateNickname(TEST_USERNAME);
		when(authenticationService.generateAccessToken(USER_TOKEN_PAYLOAD)).thenReturn(USER_ACCESS_TOKEN);
		when(authenticationService.generateRefreshToken(USER_TOKEN_PAYLOAD)).thenReturn(USER_REFRESH_TOKEN);

		// then
		mockMvc.perform(
				post("/api/v1/auth/login/{social}", TEST_SOCIAL_TYPE)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectToString(loginRequest))
			)
			.andExpect(status().isOk())
			.andDo(
				document(
					"{class-name}/user-login-success",
					pathParameters(parameterWithName("social").description("소셜 로그인 타입")),
					requestFields(
						fieldWithPath("oAuthAccessToken").description("발급 받은 AccessToken")
					),
					responseHeaders(
						headerWithName(HttpHeaders.SET_COOKIE).description("Refresh Token Cookie")
					),
					responseFields(
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("messages").description("응답 메시지").optional(),
						fieldWithPath("data.accessToken").description("엑세스 토큰")
					)
				)
			);
	}

	@Test
	void 로그인_실패_회원가입_필요() throws Exception {
		// given
		LoginRequest loginRequest = new LoginRequest(TEST_OAUTH_ACCESS_TOKEN);

		// when
		when(authenticationService.getSocialUsername(loginRequest, TEST_SOCIAL_TYPE)).thenReturn(TEST_USERNAME);
		doThrow(new IllegalArgumentException("사용자가 존재하지 않습니다")).when(authenticationService)
			.getUserRoleByUsername(TEST_USERNAME);

		// then
		mockMvc.perform(
				post("/api/v1/auth/login/{social}", TEST_SOCIAL_TYPE)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectToString(loginRequest))
			)
			.andExpect(status().isBadRequest())
			.andDo(
				document(
					"{class-name}/user-login-fail-register",
					pathParameters(parameterWithName("social").description("소셜 로그인 타입")),
					requestFields(
						fieldWithPath("oAuthAccessToken").description("발급 받은 AccessToken")
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
		LoginRequest loginRequest = LOGIN_REQUEST;

		// when
		when(authenticationService.getSocialUsername(loginRequest, TEST_SOCIAL_TYPE)).thenThrow(
			new PolzzakException(ErrorCode.OAUTH_AUTHENTICATION_FAIL));

		// then
		mockMvc.perform(
				post("/api/v1/auth/login/{social}", "kakao")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectToString(loginRequest))
			)
			.andExpect(status().isUnauthorized())
			.andDo(
				document(
					"{class-name}/user-login-fail-invalid",
					pathParameters(parameterWithName("social").description("소셜 로그인 타입")),
					requestFields(
						fieldWithPath("oAuthAccessToken").description("발급 받은 AccessToken")
					),
					responseFields(
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("messages").description("응답 메시지"),
						fieldWithPath("data").description("응답 데이터").optional()
					)
				)
			);
	}

	@Test
	void 회원가입_성공_프로필_없는_경우() throws Exception {
		// given
		RegisterRequest registerRequest = REGISTER_KID_REQUEST;
		MockMultipartFile requestPart =
			new MockMultipartFile("registerRequest", "registerRequest", "application/json",
				objectToString(registerRequest).getBytes());

		// when
		when(authenticationService.register(registerRequest, null)).thenReturn(USER_TOKEN_PAYLOAD);
		when(authenticationService.generateAccessToken(USER_TOKEN_PAYLOAD)).thenReturn(USER_ACCESS_TOKEN);
		when(authenticationService.generateRefreshToken(USER_TOKEN_PAYLOAD)).thenReturn(USER_REFRESH_TOKEN);

		// then
		mockMvc.perform(
				multipart("/api/v1/auth/register")
					.file(requestPart)
					.contentType(MediaType.MULTIPART_FORM_DATA)
			)
			.andExpect(status().isOk())
			.andDo(
				document(
					"{class-name}/user-register-success-empty-profile",
					requestParts(partWithName("registerRequest").description(
						"사용자 정보 (json)\n" + "username: 소셜 사용자 정보\n" + "memberType: 사용자 타입\n" + "socialType: 소셜 타입\n"
							+ "nickname: 닉네임\n")),
					responseHeaders(
						headerWithName(HttpHeaders.SET_COOKIE).description("Refresh Token Cookie")
					),
					responseFields(
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("messages").description("응답 메시지").optional(),
						fieldWithPath("data.accessToken").description("엑세스 토큰")
					)
				)
			);
	}

	@Test
	void 회원가입_성공_프로필_있는_경우() throws Exception {
		// given
		RegisterRequest registerRequest = REGISTER_KID_REQUEST;
		String requestString = objectToString(registerRequest);
		MockMultipartFile requestPart =
			new MockMultipartFile("registerRequest", "registerRequest", "application/json", requestString.getBytes());
		MockMultipartFile profile = TEST_PROFILE;

		// when
		when(authenticationService.register(registerRequest, profile)).thenReturn(USER_TOKEN_PAYLOAD);
		when(authenticationService.generateAccessToken(USER_TOKEN_PAYLOAD)).thenReturn(USER_ACCESS_TOKEN);
		when(authenticationService.generateRefreshToken(USER_TOKEN_PAYLOAD)).thenReturn(USER_REFRESH_TOKEN);

		// then
		mockMvc.perform(
				multipart("/api/v1/auth/register")
					.file(requestPart)
					.file(profile)
					.contentType(MediaType.MULTIPART_FORM_DATA)
			)
			.andExpect(status().isOk())
			.andDo(
				document(
					"{class-name}/user-register-success-profile",
					requestParts(
						partWithName("registerRequest").description(
							"사용자 정보 (json)\n" + "username: 소셜 사용자 정보\n" + "memberType: 사용자 타입\n" + "socialType: 소셜 타입\n"
								+ "nickname: 닉네임\n"
						),
						partWithName("profile").description("사용자 프로필")
					),
					responseHeaders(
						headerWithName(HttpHeaders.SET_COOKIE).description("Refresh Token Cookie")
					),
					responseFields(
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("messages").description("응답 메시지").optional(),
						fieldWithPath("data.accessToken").description("엑세스 토큰")
					)
				)
			);
	}

	@Test
	void 회원가입_실패_닉네임_중복() throws Exception {
		// given
		RegisterRequest registerRequest = REGISTER_KID_REQUEST;
		String requestString = objectToString(registerRequest);
		MockMultipartFile requestPart =
			new MockMultipartFile("registerRequest", "registerRequest", "application/json", requestString.getBytes());
		MockMultipartFile profile = TEST_PROFILE;

		// when
		when(authenticationService.register(registerRequest, profile)).thenThrow(
			new IllegalArgumentException("이미 존재하는 사용자입니다"));

		// then
		mockMvc.perform(
				multipart("/api/v1/auth/register")
					.file(requestPart)
					.file(profile)
					.contentType(MediaType.MULTIPART_FORM_DATA)
			)
			.andExpect(status().isBadRequest())
			.andDo(
				document(
					"{class-name}/user-register-fail-duplicate-nickname",
					requestParts(
						partWithName("registerRequest").description(
							"사용자 정보 (json)\n" + "username: 소셜 사용자 정보\n" + "memberType: 사용자 타입\n" + "socialType: 소셜 타입\n"
								+ "nickname: 중복된 닉네임\n"
						),
						partWithName("profile").description("사용자 프로필")
					),
					responseFields(
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("messages").description("응답 메시지"),
						fieldWithPath("data").description("응답 데이터").optional()
					)
				)
			);
	}

	@Test
	void 회원가입_실패_없는_사용자_타입() throws Exception {
		// given
		RegisterRequest motExistMemberTypeRequest = REGISTER_MOT_EXIST_MEMBER_TYPE_REQUEST;
		String requestString = objectToString(motExistMemberTypeRequest);
		MockMultipartFile requestPart =
			new MockMultipartFile("registerRequest", "registerRequest", "application/json", requestString.getBytes());
		MockMultipartFile profile = TEST_PROFILE;

		// when
		when(authenticationService.register(motExistMemberTypeRequest, profile)).thenThrow(
			new IllegalArgumentException("요청한 멤버 타입을 찾을 수 없습니다"));

		// then
		mockMvc.perform(
				multipart("/api/v1/auth/register")
					.file(requestPart)
					.file(profile)
					.contentType(MediaType.MULTIPART_FORM_DATA)
			)
			.andExpect(status().isBadRequest())
			.andDo(
				document(
					"{class-name}/user-register-fail-not-exist-member-type",
					requestParts(
						partWithName("registerRequest").description(
							"사용자 정보 (json)\n" + "username: 소셜 사용자 정보\n" + "memberType: 없는 사용자 타입\n"
								+ "socialType: 소셜 타입\n"
								+ "nickname: 중복된 닉네임\n"
						),
						partWithName("profile").description("사용자 프로필")
					),
					responseFields(
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("messages").description("응답 메시지"),
						fieldWithPath("data").description("응답 데이터").optional()
					)
				)
			);
	}

	@Test
	void 닉네임_중복_없음() throws Exception {
		// given
		String nickname = TEST_NICKNAME;

		// when
		doNothing().when(authenticationService).validateNickname(nickname);

		// then
		mockMvc.perform(
				get("/api/v1/auth/validate/nickname")
					.param("value", nickname)
			)
			.andExpect(status().isNoContent())
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
		doThrow(new IllegalArgumentException("이미 존재하는 사용자입니다")).when(authenticationService)
			.validateNickname(nickname);

		// then
		mockMvc.perform(
				get("/api/v1/auth/validate/nickname")
					.param("value", nickname)
			)
			.andExpect(status().isBadRequest())
			.andDo(
				document(
					"{class-name}/user-valid-nickname-duplication",
					queryParameters(parameterWithName("value").description("중복된 닉네임")),
					responseFields(
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("messages").description("응답 메시지"),
						fieldWithPath("data").description("응답 데이터").optional()
					)
				)
			);
	}
}
