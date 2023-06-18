package com.polzzak.domain.family.controller;

import static com.polzzak.support.FamilyFixtures.*;
import static com.polzzak.support.TokenFixtures.*;
import static com.polzzak.support.UserFixtures.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.polzzak.domain.family.dto.FamilyMapRequest;
import com.polzzak.domain.family.dto.FamilyMemberDto;
import com.polzzak.domain.family.dto.SearchedMemberDto;
import com.polzzak.domain.family.service.FamilyMapService;
import com.polzzak.support.test.ControllerTestHelper;

@WebMvcTest(FamilyRestController.class)
class FamilyRestControllerTest extends ControllerTestHelper {

	@MockBean
	FamilyMapService familyMapService;

	@Test
	void 사용자_닉네임_검색_성공() throws Exception {
		// given
		String accessToken = USER_ACCESS_TOKEN;
		String nickname = TEST_NICKNAME;
		String username = TEST_USERNAME;
		SearchedMemberDto memberDto = SEARCHED_KID_MEMBER_DTO;

		// when
		when(familyMapService.getSearchedMemberByNickname(username, nickname)).thenReturn(memberDto);

		// then
		mockMvc.perform(
				get("/api/v1/families/users")
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + accessToken)
					.param("nickname", nickname)
			)
			.andExpectAll(status().isOk())
			.andDo(
				document(
					"{class-name}/search-nickname-success",
					requestHeaders(
						headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
					),
					queryParameters(
						parameterWithName("nickname").description("닉네임")
					),
					responseFields(
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("messages").description("응답 메시지").optional(),
						fieldWithPath("data.memberId").description("사용자 ID"),
						fieldWithPath("data.nickname").description("닉네임"),
						fieldWithPath("data.memberType.name").description("사용자 타입"),
						fieldWithPath("data.memberType.detail").description("타입 세부 내용"),
						fieldWithPath("data.profileUrl").description("프로필 Url"),
						fieldWithPath("data.familyStatus").description(
							"연동 상태 (NONE(NONE), RECEIVED(나에게 요청 보낸 사람), SENT(내가 요청 보낸 사람), APPROVE(승인)")
					)
				)
			);
	}

	@Test
	void 연동_신청_성공() throws Exception {
		// given
		String accessToken = USER_ACCESS_TOKEN;
		String username = TEST_USERNAME;
		FamilyMapRequest familyMapRequest = new FamilyMapRequest(TEST_MEMBER_ID);

		// when
		doNothing().when(familyMapService).saveFamilyTempMap(username, familyMapRequest);

		// then
		mockMvc.perform(
				post("/api/v1/families")
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectToString(familyMapRequest))
			)
			.andExpectAll(status().isCreated())
			.andDo(
				document(
					"{class-name}/create-family-map-success",
					requestHeaders(
						headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
					),
					requestFields(
						fieldWithPath("targetId").description("요청할 사용자 ID")
					),
					responseFields(
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("messages").description("응답 메시지").optional(),
						fieldWithPath("data").description("응답 데이터").optional()
					)
				)
			);
	}

	@Test
	void 연동_신청_실패_중복_요청() throws Exception {
		// given
		String accessToken = USER_ACCESS_TOKEN;
		String username = TEST_USERNAME;
		FamilyMapRequest familyMapRequest = new FamilyMapRequest(TEST_MEMBER_ID);

		// when
		doThrow(new IllegalArgumentException("중복된 요청입니다")).when(familyMapService)
			.saveFamilyTempMap(username, familyMapRequest);

		// then
		mockMvc.perform(
				post("/api/v1/families")
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectToString(familyMapRequest))
			)
			.andExpectAll(status().isBadRequest())
			.andDo(
				document(
					"{class-name}/create-family-map-fail",
					requestHeaders(
						headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
					),
					requestFields(
						fieldWithPath("targetId").description("중복 신청한 사용자 ID")
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
	void 연동_요청_승인_성공() throws Exception {
		// given
		String accessToken = USER_ACCESS_TOKEN;
		String username = TEST_USERNAME;
		String id = String.valueOf(TEST_MEMBER_ID);

		// when
		doNothing().when(familyMapService).approveFamilyMap(username, TEST_MEMBER_ID);

		// then
		mockMvc.perform(
				patch("/api/v1/families/approve/{id}", id)
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + accessToken)
			)
			.andExpectAll(status().isNoContent())
			.andDo(
				document(
					"{class-name}/approve-family-map-success",
					requestHeaders(
						headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
					),
					pathParameters(
						parameterWithName("id").description("승인할 사용자 ID")
					)
				)
			);
	}

	@Test
	void 연동_삭제_성공() throws Exception {
		// given
		String accessToken = USER_ACCESS_TOKEN;
		String username = TEST_USERNAME;
		String id = String.valueOf(TEST_MEMBER_ID);

		// when
		doNothing().when(familyMapService).deleteFamilyMap(username, TEST_MEMBER_ID);

		// then
		mockMvc.perform(
				delete("/api/v1/families/{id}", id)
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + accessToken)
			)
			.andExpectAll(status().isNoContent())
			.andDo(
				document(
					"{class-name}/delete-family-map-success",
					requestHeaders(
						headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
					),
					pathParameters(
						parameterWithName("id").description("연동 삭제할 사용자 ID")
					)
				)
			);
	}

	@Test
	void 연동_요청_거절_성공() throws Exception {
		// given
		String accessToken = USER_ACCESS_TOKEN;
		String username = TEST_USERNAME;
		String id = String.valueOf(TEST_MEMBER_ID);

		// when
		doNothing().when(familyMapService).rejectFamilyRequest(username, TEST_MEMBER_ID);

		// then
		mockMvc.perform(
				delete("/api/v1/families/reject/{id}", id)
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + accessToken)
			)
			.andExpectAll(status().isNoContent())
			.andDo(
				document(
					"{class-name}/reject-family-map-success",
					requestHeaders(
						headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
					),
					pathParameters(
						parameterWithName("id").description("연동 요청 거절할 사용자 ID")
					)
				)
			);
	}

	@Test
	void 연동_요청_취소_성공() throws Exception {
		// given
		String accessToken = USER_ACCESS_TOKEN;
		String username = TEST_USERNAME;
		String id = String.valueOf(TEST_MEMBER_ID);

		// when
		doNothing().when(familyMapService).cancelFamilyRequest(username, TEST_MEMBER_ID);

		// then
		mockMvc.perform(
				delete("/api/v1/families/cancel/{id}", id)
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + accessToken)
			)
			.andExpectAll(status().isNoContent())
			.andDo(
				document(
					"{class-name}/cancel-family-map-success",
					requestHeaders(
						headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
					),
					pathParameters(
						parameterWithName("id").description("연동 요청 취소할 사용자 ID")
					)
				)
			);
	}

	@Test
	void 연동된_사용자_목록_조회_성공() throws Exception {
		// given
		String accessToken = USER_ACCESS_TOKEN;
		String username = TEST_USERNAME;
		List<FamilyMemberDto> familyMemberDtos = List.of(FAMILY_KID_MEMBER_DTO);

		// when
		when(familyMapService.getMyFamilies(username)).thenReturn(familyMemberDtos);

		// then
		mockMvc.perform(
				get("/api/v1/families")
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + accessToken)
			)
			.andExpectAll(status().isOk())
			.andDo(
				document(
					"{class-name}/get-families-success",
					requestHeaders(
						headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
					),
					responseFields(
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("messages").description("응답 메시지").optional(),
						fieldWithPath("data.families").description("연동된 사용자 목록"),
						fieldWithPath("data.families[0].memberId").description("사용자 ID"),
						fieldWithPath("data.families[0].nickname").description("닉네임"),
						fieldWithPath("data.families[0].memberType.name").description("사용자 타입"),
						fieldWithPath("data.families[0].memberType.detail").description("타입 세부 내용"),
						fieldWithPath("data.families[0].profileUrl").description("프로필 Url")
					)
				)
			);
	}

	@Test
	void 연동_요청보낸_사용자_목록_조회_성공() throws Exception {
		// given
		String accessToken = USER_ACCESS_TOKEN;
		String username = TEST_USERNAME;
		List<FamilyMemberDto> familyMemberDtos = List.of(FAMILY_KID_MEMBER_DTO);

		// when
		when(familyMapService.getMySentList(username)).thenReturn(familyMemberDtos);

		// then
		mockMvc.perform(
				get("/api/v1/families/requests/sent")
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + accessToken)
			)
			.andExpectAll(status().isOk())
			.andDo(
				document(
					"{class-name}/get-sent-users-success",
					requestHeaders(
						headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
					),
					responseFields(
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("messages").description("응답 메시지").optional(),
						fieldWithPath("data.families").description("연동 요청보낸 사용자 목록"),
						fieldWithPath("data.families[0].memberId").description("사용자 ID"),
						fieldWithPath("data.families[0].nickname").description("닉네임"),
						fieldWithPath("data.families[0].memberType.name").description("사용자 타입"),
						fieldWithPath("data.families[0].memberType.detail").description("타입 세부 내용"),
						fieldWithPath("data.families[0].profileUrl").description("프로필 Url")
					)
				)
			);
	}

	@Test
	void 연동_요청받은_사용자_목록_조회_성공() throws Exception {
		// given
		String accessToken = USER_ACCESS_TOKEN;
		String username = TEST_USERNAME;
		List<FamilyMemberDto> familyMemberDtos = List.of(FAMILY_KID_MEMBER_DTO);

		// when
		when(familyMapService.getMyReceivedList(username)).thenReturn(familyMemberDtos);

		mockMvc.perform(
				get("/api/v1/families/requests/received")
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + accessToken)
			)
			.andExpectAll(status().isOk())
			.andDo(
				document(
					"{class-name}/get-received-users-success",
					requestHeaders(
						headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
					),
					responseFields(
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("messages").description("응답 메시지").optional(),
						fieldWithPath("data.families").description("연동 요청받은 사용자 목록"),
						fieldWithPath("data.families[0].memberId").description("사용자 ID"),
						fieldWithPath("data.families[0].nickname").description("닉네임"),
						fieldWithPath("data.families[0].memberType.name").description("사용자 타입"),
						fieldWithPath("data.families[0].memberType.detail").description("타입 세부 내용"),
						fieldWithPath("data.families[0].profileUrl").description("프로필 Url")
					)
				)
			);
	}

	@Test
	void 새로운_연동_요청_마커_조회_성공() throws Exception {
		// when & then
		when(familyMapService.getNewRequestMark(TEST_USERNAME)).thenReturn(FAMILY_NEW_REQUEST_MARKER_DTO);

		mockMvc.perform(
				get("/api/v1/families/requests")
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + USER_ACCESS_TOKEN)
			)
			.andExpectAll(status().isOk())
			.andDo(
				document(
					"{class-name}/get-new-request-marker-success",
					requestHeaders(
						headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
					),
					responseFields(
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("messages").description("응답 메시지").optional(),
						fieldWithPath("data.isFamilyReceived").description("연동 요청을 받았는지 여부"),
						fieldWithPath("data.isFamilySent").description("연동 신청을 받았는지 여부")
					)
				)
			);
	}
}
