package com.polzzak.domain.memberpoint.controller;

import static com.polzzak.support.MemberPointFixtures.*;
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

import com.polzzak.domain.memberpoint.service.MemberPointService;
import com.polzzak.support.test.ControllerTestHelper;

@WebMvcTest(MemberPointRestController.class)
class MemberPointRestControllerTest extends ControllerTestHelper {
	@MockBean
	MemberPointService memberPointService;

	@Test
	void 나의_포인트_조회() throws Exception {
		when(memberPointService.getMyMemberPoint(TEST_MEMBER_ID)).thenReturn(TEST_MEMBER_POINT_RESPONSE);

		mockMvc.perform(
				get("/api/v1/member-points/me")
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + USER_ACCESS_TOKEN)
			)
			.andExpectAll(status().isOk())
			.andDo(
				document(
					"{class-name}/get-my-member-points-success",
					requestHeaders(
						headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
					),
					responseFields(
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("messages").description("응답 메시지").optional(),
						fieldWithPath("data.point").description("사용자 포인트"),
						fieldWithPath("data.level").description("사용자 레벨")
					)
				)
			);
	}

	@Test
	void 나의_포인트_내역_조회() throws Exception {
		when(memberPointService.getMyEarningHistories(TEST_MEMBER_ID, 0, 10)).thenReturn(
			TEST_MEMBER_POINT_HISTORY_SLICE_RESPONSE);

		mockMvc.perform(
				get("/api/v1/member-points/earning-histories/me")
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + USER_ACCESS_TOKEN)
			)
			.andExpectAll(status().isOk())
			.andDo(
				document(
					"{class-name}/get-my-earning-histories-success",
					requestHeaders(
						headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
					),
					queryParameters(
						parameterWithName("startId").description("조회 시작 아이디 (default = 0)").optional(),
						parameterWithName("size").description("조회할 개수 (default = 10)").optional()
					),
					responseFields(
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("messages").description("응답 메시지").optional(),
						fieldWithPath("data.startId").description("조회 시작 아이디, null이면 더 이상 데이터가 존재하지 않음").optional(),
						fieldWithPath("data.content[].description").description("포인트 적립 내역 설명"),
						fieldWithPath("data.content[].increasedPoint").description("적립 포인트"),
						fieldWithPath("data.content[].remainingPoint").description("적립 후 총 포인트"),
						fieldWithPath("data.content[].createdDate").description("적립 일자")
					)
				)
			);
	}
}
