package com.polzzak.domain.membertype.controller;

import static com.polzzak.support.MemberTypeFixtures.*;
import static com.polzzak.support.TokenFixtures.*;
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

import com.polzzak.domain.membertype.dto.MemberTypeDetailRequest;
import com.polzzak.domain.membertype.service.MemberTypeDetailService;
import com.polzzak.support.test.ControllerTestHelper;

@WebMvcTest(MemberTypeRestController.class)
class MemberTypeRestControllerTest extends ControllerTestHelper {

	@MockBean
	MemberTypeDetailService memberTypeDetailService;

	@Test
	void 멤버_타입_목록_조회_성공() throws Exception {
		// when
		when(memberTypeDetailService.findMemberTypeDetailList()).thenReturn(MEMBER_TYPE_DETAIL_LIST_DTO);

		// then
		mockMvc.perform(
				get("/api/v1/member-types")
			)
			.andExpect(status().isOk())
			.andDo(document(
				"{class-name}/get-member-type-detail-list-success",
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("messages").description("응답 메시지"),
					fieldWithPath("data.memberTypeDetailList[0].memberTypeDetailId").description("memberTypeDetail ID"),
					fieldWithPath("data.memberTypeDetailList[0].detail").description("타입 내용")
				)
			));
	}

	@Test
	void 멤버_타입_생성_성공() throws Exception {
		// given
		MemberTypeDetailRequest memberTypeDetailRequest = MEMBER_TYPE_DETAIL_REQUEST;

		// when
		doNothing().when(memberTypeDetailService).saveMemberTypeDetail(memberTypeDetailRequest);

		// then
		mockMvc.perform(
				post("/api/v1/member-types")
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + ADMIN_ACCESS_TOKEN)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectToString(memberTypeDetailRequest))
			)
			.andExpect(status().isCreated())
			.andDo(document(
				"{class-name}/create-member-type-detail-success",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("관리자 엑세스 토큰")
				),
				requestFields(
					fieldWithPath("memberType").description("사용자 타입"),
					fieldWithPath("detail").description("세부 내용")
				)
			));
	}

	@Test
	void 멤버_타입_생성_실패_사용자_권한() throws Exception {
		// given
		MemberTypeDetailRequest memberTypeDetailRequest = MEMBER_TYPE_DETAIL_REQUEST;

		// then
		mockMvc.perform(
				post("/api/v1/member-types")
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + USER_ACCESS_TOKEN)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectToString(memberTypeDetailRequest))
			)
			.andExpect(status().isForbidden())
			.andDo(document(
				"{class-name}/create-member-type-detail-fail-user-role",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("사용자 엑세스 토큰")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("messages").description("응답 메시지"),
					fieldWithPath("data").description("응답 데이터")
				)
			));
	}

	@Test
	void 멤버_타입_생성_실패_중복된_세부_내용() throws Exception {
		// given
		MemberTypeDetailRequest memberTypeDetailRequest = MEMBER_TYPE_DETAIL_REQUEST;

		// when
		doThrow(new IllegalArgumentException("이미 존재하는 타입입니다")).when(memberTypeDetailService)
			.saveMemberTypeDetail(memberTypeDetailRequest);

		// then
		mockMvc.perform(
				post("/api/v1/member-types")
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + ADMIN_ACCESS_TOKEN)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectToString(memberTypeDetailRequest))
			)
			.andExpect(status().isBadRequest())
			.andDo(document(
				"{class-name}/create-member-type-detail-fail-duplicate-detail",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("관리자 엑세스 토큰")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("messages").description("응답 메시지"),
					fieldWithPath("data").description("응답 데이터")
				)
			));
	}

	@Test
	void 멤버_타입_삭제_성공() throws Exception {
		// when
		doNothing().when(memberTypeDetailService).deleteMemberTypeDetailById(TEST_DETAIL_ID);

		// then
		mockMvc.perform(
				delete("/api/v1/member-types/{id}", TEST_DETAIL_ID)
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + ADMIN_ACCESS_TOKEN)
			)
			.andExpect(status().isNoContent())
			.andDo(document(
				"{class-name}/delete-member-type-detail-success",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("관리자 엑세스 토큰")
				),
				pathParameters(
					parameterWithName("id").description("수정할 memberTypeDetail id")
				)
			));
	}

	@Test
	void 멤버_타입_삭제_실패_사용자_권한() throws Exception {
		// then
		mockMvc.perform(
				delete("/api/v1/member-types/{id}", TEST_DETAIL_ID)
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + USER_ACCESS_TOKEN)
			)
			.andExpect(status().isForbidden())
			.andDo(document(
				"{class-name}/delete-member-type-detail-fail-user-role",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("사용자 엑세스 토큰")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("messages").description("응답 메시지"),
					fieldWithPath("data").description("응답 데이터")
				)
			));
	}

	@Test
	void 멤버_타입_수정_성공() throws Exception {
		// given
		MemberTypeDetailRequest memberTypeDetailRequest = MEMBER_TYPE_DETAIL_REQUEST;

		// when
		doNothing().when(memberTypeDetailService).updateMemberTypeDetail(TEST_DETAIL_ID, memberTypeDetailRequest);

		// then
		mockMvc.perform(
				put("/api/v1/member-types/{id}", TEST_DETAIL_ID)
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + ADMIN_ACCESS_TOKEN)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectToString(memberTypeDetailRequest))
			)
			.andExpect(status().isNoContent())
			.andDo(document(
				"{class-name}/update-member-type-detail-success",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("관리자 엑세스 토큰")
				),
				pathParameters(
					parameterWithName("id").description("수정할 memberTypeDetail id")
				),
				requestFields(
					fieldWithPath("memberType").description("사용자 타입"),
					fieldWithPath("detail").description("세부 내용")
				)
			));
	}

	@Test
	void 멤버_타입_수정_실패_사용자_권한() throws Exception {
		// then
		mockMvc.perform(
				delete("/api/v1/member-types/{id}", TEST_DETAIL_ID)
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + USER_ACCESS_TOKEN)
			)
			.andExpect(status().isForbidden())
			.andDo(document(
				"{class-name}/update-member-type-detail-fail-user-role",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("사용자 엑세스 토큰")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("messages").description("응답 메시지"),
					fieldWithPath("data").description("응답 데이터")
				)
			));
	}

	@Test
	void 멤버_타입_수정_실패_중복된_세부_내용() throws Exception {
		// given
		MemberTypeDetailRequest duplicatedMemberTypeDetailRequest = DUPLICATED_MEMBER_TYPE_DETAIL_REQUEST;

		// when
		doThrow(new IllegalArgumentException("이미 존재하는 타입입니다")).when(memberTypeDetailService)
			.updateMemberTypeDetail(TEST_DETAIL_ID, duplicatedMemberTypeDetailRequest);

		// then
		mockMvc.perform(
				put("/api/v1/member-types/{id}", TEST_DETAIL_ID)
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + ADMIN_ACCESS_TOKEN)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectToString(duplicatedMemberTypeDetailRequest))
			)
			.andExpect(status().isBadRequest())
			.andDo(document(
				"{class-name}/update-member-type-detail-fail-duplicated-detail",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("관리자 엑세스 토큰")
				),
				pathParameters(
					parameterWithName("id").description("수정할 memberTypeDetail id")
				),
				requestFields(
					fieldWithPath("memberType").description("사용자 타입"),
					fieldWithPath("detail").description("중복된 세부 내용")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("messages").description("응답 메시지"),
					fieldWithPath("data").description("응답 데이터")
				)
			));
	}

	@Test
	void 멤버_타입_수정_실패_없는_세부_내용() throws Exception {
		// given
		MemberTypeDetailRequest memberTypeDetailRequest = MEMBER_TYPE_DETAIL_REQUEST;

		// when
		doThrow(new IllegalArgumentException("요청한 멤버 타입을 찾을 수 없습니다")).when(memberTypeDetailService)
			.updateMemberTypeDetail(TEST_NOT_EXIST_DETAIL_ID, memberTypeDetailRequest);

		// then
		mockMvc.perform(
				put("/api/v1/member-types/{id}", TEST_NOT_EXIST_DETAIL_ID)
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + ADMIN_ACCESS_TOKEN)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectToString(memberTypeDetailRequest))
			)
			.andExpect(status().isBadRequest())
			.andDo(document(
				"{class-name}/update-member-type-detail-fail-not-exist-detail",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("관리자 엑세스 토큰")
				),
				pathParameters(
					parameterWithName("id").description("수정할 memberTypeDetail id")
				),
				requestFields(
					fieldWithPath("memberType").description("사용자 타입"),
					fieldWithPath("detail").description("세부 내용")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("messages").description("응답 메시지"),
					fieldWithPath("data").description("응답 데이터")
				)
			));
	}
}
