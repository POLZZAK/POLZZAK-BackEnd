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

import com.polzzak.domain.user.dto.UserDto;
import com.polzzak.domain.user.entity.MemberType;
import com.polzzak.domain.user.entity.SocialType;
import com.polzzak.domain.user.service.UserService;
import com.polzzak.support.test.ControllerTestHelper;

@WebMvcTest(UserRestController.class)
class UserRestControllerTest extends ControllerTestHelper {

	@MockBean
	UserService userService;

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
