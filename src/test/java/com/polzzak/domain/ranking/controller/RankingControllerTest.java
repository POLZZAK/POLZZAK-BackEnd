package com.polzzak.domain.ranking.controller;

import static com.polzzak.support.RankingFixtures.GUARDIAN_RANKING_SUMMARY_LIST_RESPONSE;
import static com.polzzak.support.RankingFixtures.KID_RANKING_SUMMARY_LIST_RESPONSE;
import static com.polzzak.support.TokenFixtures.TOKEN_TYPE;
import static com.polzzak.support.TokenFixtures.USER_ACCESS_TOKEN;
import static com.polzzak.support.UserFixtures.TEST_MEMBER_ID;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;

import com.polzzak.domain.ranking.service.RankingService;
import com.polzzak.support.test.ControllerTestHelper;

@WebMvcTest(RankingController.class)
class RankingControllerTest extends ControllerTestHelper {
	@MockBean
	RankingService rankingService;

	@Test
	void 보호자_랭킹_조회() throws Exception {
		when(rankingService.getGuardianRankingSummaries(TEST_MEMBER_ID)).thenReturn(
			GUARDIAN_RANKING_SUMMARY_LIST_RESPONSE);
		mockMvc.perform(
				get("/api/v1/rankings/guardians")
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + USER_ACCESS_TOKEN)
			)
			.andExpectAll(status().isOk())
			.andDo(
				document(
					"{class-name}/get-guardian-ranking-summaries",
					responseFields(
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("messages").description("응답 메시지").optional(),
						fieldWithPath("data.memberSimpleResponse.memberId").description("본인 아이디"),
						fieldWithPath("data.memberSimpleResponse.nickname").description("본인 닉네임"),
						fieldWithPath("data.memberSimpleResponse.memberPoint.point").description("본인 포인트"),
						fieldWithPath("data.memberSimpleResponse.memberPoint.level").description("본인 레벨"),
						fieldWithPath("data.memberSimpleResponse.memberType.name").description("본인 타입"),
						fieldWithPath("data.memberSimpleResponse.memberType.detail").description(
							"본인 타입 상세"),
						fieldWithPath("data.memberSimpleResponse.profileUrl").description("본인 프로필"),
						fieldWithPath("data.memberSimpleResponse.myRanking").description("본인 랭킹"),
						fieldWithPath("data.rankingSummaries[].ranking").description("사용자 랭킹"),
						fieldWithPath("data.rankingSummaries[].rankingStatus").description("사용자 랭킹 변화"),
						fieldWithPath("data.rankingSummaries[].nickname").description("사용자 닉네임"),
						fieldWithPath("data.rankingSummaries[].point").description("사용자 포인트"),
						fieldWithPath("data.rankingSummaries[].level").description("사용자 레벨"),
						fieldWithPath("data.rankingSummaries[].memberTypeDetail").description("보호자 타입"),
						fieldWithPath("data.rankingSummaries[].profileUrl").description("사용자 프로필"),
						fieldWithPath("data.rankingSummaries[].isMe").description("본인 여부")
					)
				)
			);
	}

	@Test
	void 아이_랭킹_조회() throws Exception {
		when(rankingService.getKidRankingSummaries(TEST_MEMBER_ID)).thenReturn(KID_RANKING_SUMMARY_LIST_RESPONSE);
		mockMvc.perform(
				get("/api/v1/rankings/kids")
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + USER_ACCESS_TOKEN)
			)
			.andExpectAll(status().isOk())
			.andDo(
				document(
					"{class-name}/get-kid-ranking-summaries",
					responseFields(
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("messages").description("응답 메시지").optional(),
						fieldWithPath("data.memberSimpleResponse.memberId").description("본인 아이디"),
						fieldWithPath("data.memberSimpleResponse.nickname").description("본인 닉네임"),
						fieldWithPath("data.memberSimpleResponse.memberPoint.point").description("본인 포인트"),
						fieldWithPath("data.memberSimpleResponse.memberPoint.level").description("본인 레벨"),
						fieldWithPath("data.memberSimpleResponse.memberType.name").description("본인 타입"),
						fieldWithPath("data.memberSimpleResponse.memberType.detail").description(
							"본인 타입 상세"),
						fieldWithPath("data.memberSimpleResponse.profileUrl").description("본인 프로필"),
						fieldWithPath("data.memberSimpleResponse.myRanking").description("본인 랭킹"),
						fieldWithPath("data.rankingSummaries[].ranking").description("사용자 랭킹"),
						fieldWithPath("data.rankingSummaries[].rankingStatus").description("사용자 랭킹 변화"),
						fieldWithPath("data.rankingSummaries[].nickname").description("사용자 닉네임"),
						fieldWithPath("data.rankingSummaries[].point").description("사용자 포인트"),
						fieldWithPath("data.rankingSummaries[].level").description("사용자 레벨"),
						fieldWithPath("data.rankingSummaries[].profileUrl").description("사용자 프로필"),
						fieldWithPath("data.rankingSummaries[].isMe").description("본인 여부")
					)
				)
			);
	}
}
