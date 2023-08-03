package com.polzzak.domain.ranking.controller;

import static com.polzzak.support.RankingFixtures.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.polzzak.domain.ranking.service.RankingService;
import com.polzzak.support.test.ControllerTestHelper;

@WebMvcTest(RankingController.class)
class RankingControllerTest extends ControllerTestHelper {
	@MockBean
	RankingService rankingService;

	@Test
	void 보호자_랭킹_조회() throws Exception {
		when(rankingService.getGuardianRankingSummaries()).thenReturn(GUARDIAN_RANKING_SUMMARY_LIST_RESPONSE);
		mockMvc.perform(
				get("/api/v1/rankings/guardians")
			)
			.andExpectAll(status().isOk())
			.andDo(
				document(
					"{class-name}/get-guardian-ranking-summaries",
					responseFields(
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("messages").description("응답 메시지").optional(),
						fieldWithPath("data.rankingSummaries[].ranking").description("사용자 랭킹"),
						fieldWithPath("data.rankingSummaries[].rankingStatus").description("사용자 랭킹 변화"),
						fieldWithPath("data.rankingSummaries[].nickname").description("사용자 닉네임"),
						fieldWithPath("data.rankingSummaries[].point").description("사용자 포인트"),
						fieldWithPath("data.rankingSummaries[].level").description("사용자 레벨"),
						fieldWithPath("data.rankingSummaries[].memberTypeDetail").description("보호자 타입"),
						fieldWithPath("data.rankingSummaries[].profileUrl").description("사용자 프로필")
					)
				)
			);
	}

	@Test
	void 아이_랭킹_조회() throws Exception {
		when(rankingService.getKidRankingSummaries()).thenReturn(KID_RANKING_SUMMARY_LIST_RESPONSE);
		mockMvc.perform(
				get("/api/v1/rankings/kids")
			)
			.andExpectAll(status().isOk())
			.andDo(
				document(
					"{class-name}/get-kid-ranking-summaries",
					responseFields(
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("messages").description("응답 메시지").optional(),
						fieldWithPath("data.rankingSummaries[].ranking").description("사용자 랭킹"),
						fieldWithPath("data.rankingSummaries[].rankingStatus").description("사용자 랭킹 변화"),
						fieldWithPath("data.rankingSummaries[].nickname").description("사용자 닉네임"),
						fieldWithPath("data.rankingSummaries[].point").description("사용자 포인트"),
						fieldWithPath("data.rankingSummaries[].level").description("사용자 레벨"),
						fieldWithPath("data.rankingSummaries[].profileUrl").description("사용자 프로필")
					)
				)
			);
	}
}
