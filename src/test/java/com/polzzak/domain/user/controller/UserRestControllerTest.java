package com.polzzak.domain.user.controller;

import static com.polzzak.support.TokenFixtures.TOKEN_TYPE;
import static com.polzzak.support.TokenFixtures.USER_ACCESS_TOKEN;
import static com.polzzak.support.UserFixtures.MEMBER_RESPONSE;
import static com.polzzak.support.UserFixtures.TEST_MEMBER_ID;
import static com.polzzak.support.UserFixtures.TEST_PREV_PROFILE_KEY;
import static com.polzzak.support.UserFixtures.TEST_PROFILE;
import static com.polzzak.support.UserFixtures.TEST_PROFILE_KEY;
import static com.polzzak.support.UserFixtures.UPDATE_NICKNAME_REQUEST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.polzzak.domain.user.service.UserService;
import com.polzzak.support.test.ControllerTestHelper;

@WebMvcTest(UserRestController.class)
class UserRestControllerTest extends ControllerTestHelper {

	@MockBean
	UserService userService;

	@Test
	void 사용자_정보_조회_성공() throws Exception {
		when(userService.getMemberResponse(TEST_MEMBER_ID)).thenReturn(MEMBER_RESPONSE);

		mockMvc.perform(
				get("/api/v1/users/me")
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + USER_ACCESS_TOKEN)
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
						fieldWithPath("messages").description("응답 메시지").optional(),
						fieldWithPath("data.memberId").description("사용자 ID"),
						fieldWithPath("data.nickname").description("닉네임"),
						fieldWithPath("data.memberPoint.point").description("사용자 포인트"),
						fieldWithPath("data.memberPoint.level").description("사용자 레벨"),
						fieldWithPath("data.memberType.name").description("사용자 타입"),
						fieldWithPath("data.memberType.detail").description("타입 세부 내용"),
						fieldWithPath("data.profileUrl").description("프로필 Url"),
						fieldWithPath("data.familyCount").description("연동된 가족 수")
					)
				)
			);
	}

	@Test
	void 사용자_프로필_변경_성공() throws Exception {
		when(userService.uploadProfile(any())).thenReturn(TEST_PROFILE_KEY);
		when(userService.updateMemberProfile(TEST_MEMBER_ID, TEST_PROFILE_KEY)).thenReturn(
			Optional.of(TEST_PREV_PROFILE_KEY));
		doNothing().when(userService).deleteProfile(TEST_PREV_PROFILE_KEY);
		mockMvc.perform(
				multipart("/api/v1/users/profile")
					.file(TEST_PROFILE)
					.contentType(MediaType.MULTIPART_FORM_DATA)
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + USER_ACCESS_TOKEN)
					.with(request -> {
						request.setMethod("PATCH");
						return request;
					})
			)
			.andExpect(status().isNoContent())
			.andDo(
				document(
					"{class-name}/user-update-profile-success",
					requestHeaders(
						headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
					),
					requestParts(
						partWithName("profile").description("수정할 사용자 프로필")
					)
				)
			);
	}

	@Test
	void 사용자_닉네임_변경_성공() throws Exception {
		doNothing().when(userService).updateNickname(any(), any());
		mockMvc.perform(
				patch("/api/v1/users/nickname")
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + USER_ACCESS_TOKEN)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectToString(UPDATE_NICKNAME_REQUEST))
			)
			.andExpectAll(status().isNoContent())
			.andDo(
				document(
					"{class-name}/user-update-nickname-success",
					requestHeaders(
						headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
					),
					requestFields(
						fieldWithPath("nickname").description("수정할 닉네임")
					)
				)
			);
	}

	@Test
	void 회원_탈퇴_성공() throws Exception {
		doNothing().when(userService).withdrawMember(any());
		mockMvc.perform(
				delete("/api/v1/users")
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + USER_ACCESS_TOKEN)
			)
			.andExpectAll(status().isNoContent())
			.andDo(
				document(
					"{class-name}/user-delete-success",
					requestHeaders(
						headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
					)
				)
			);
	}
}
