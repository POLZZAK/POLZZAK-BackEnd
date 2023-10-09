package com.polzzak.domain.pushtoken.controller;

import static com.polzzak.support.TokenFixtures.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.polzzak.domain.pushtoken.service.PushTokenService;
import com.polzzak.domain.user.service.UserService;
import com.polzzak.support.PushTokenFixtures;
import com.polzzak.support.test.ControllerTestHelper;

@WebMvcTest(PushTokenController.class)
class PushTokenControllerTest extends ControllerTestHelper {

	@MockBean
	private PushTokenService pushTokenService;
	@MockBean
	private UserService userService;

	private static final String BASE_URL = "/api/v1/push-token";

	@BeforeEach
	public void setup() {
		when(userService.findMemberByMemberId(anyLong())).thenReturn(PushTokenFixtures.MEMBER);
	}

	@Test
	@DisplayName("푸시 토큰 생성 테스트")
	void createStampBoardTest() throws Exception {
		doNothing().when(pushTokenService).addToken(anyLong(), anyString());

		mockMvc.perform(
				post(BASE_URL)
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + USER_ACCESS_TOKEN)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectToString(PushTokenFixtures.CREATE_PUSH_TOKEN)))
			.andDo(print())
			.andExpect(status().isCreated())
			.andDo(document("push/push_token-create-success",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
				),
				requestFields(
					fieldWithPath("token").description("푸시 토큰")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("messages").description("응답 메시지").optional(),
					fieldWithPath("data").description("응답 데이터").optional()
				)));
	}
}
