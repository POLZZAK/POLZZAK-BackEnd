package com.polzzak.domain.user.controller;

import static com.polzzak.support.TokenFixtures.*;
import static com.polzzak.support.UserFixtures.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;

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
}
