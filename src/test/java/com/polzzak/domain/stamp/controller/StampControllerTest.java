package com.polzzak.domain.stamp.controller;

import static com.polzzak.support.StampFixtures.*;
import static com.polzzak.support.TokenFixtures.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.polzzak.domain.stamp.service.StampBoardService;
import com.polzzak.domain.stamp.service.StampService;
import com.polzzak.domain.user.service.UserService;
import com.polzzak.support.test.ControllerTestHelper;

@WebMvcTest(StampController.class)
class StampControllerTest extends ControllerTestHelper {

	@MockBean
	private StampBoardService stampBoardService;
	@MockBean
	private UserService userService;
	@MockBean
	private StampService stampService;

	private static final String BASE_URL = "/api/v1/stamp";

	@BeforeEach
	public void setup() {
		when(userService.getMemberInfo(anyString())).thenReturn(GUARDIAN);
	}

	@Test
	@DisplayName("도장판 생성 테스트")
	void createStampBoardTest() throws Exception {
		doNothing().when(stampBoardService).createStampBoard(any(), any());

		mockMvc.perform(
				post(BASE_URL + "/stamp-board")
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + ACCESS_TOKEN)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectToString(STAMP_BOARD_CREATE_REQUEST)))
			.andDo(print())
			.andExpect(status().isCreated())
			.andDo(document("stamp/board-create-success",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
				),
				requestFields(
					fieldWithPath("kidId").description("도장판을 생성할 kid ID"),
					fieldWithPath("name").description("도장판 이름"),
					fieldWithPath("goalStampCount").description("목표 도장 개수"),
					fieldWithPath("reward").description("보상 쿠폰"),
					fieldWithPath("missionContents[]").description("미션 내용 list")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("messages").description("응답 메시지"),
					fieldWithPath("data").description("응답 데이터")
				)));
	}

	@Test
	@DisplayName("메인 페이지 도장판 목록 조회 테스트")
	void getStampBoardsTest() throws Exception {
		//FIXME jjh anyLong() error -> isNull()
		when(stampBoardService.getFamilyStampBoardSummaries(any(), isNull(), anyBoolean())).thenReturn(
			FAMILY_STAMP_BOARD_SUMMARIES);

		mockMvc.perform(
				get(BASE_URL + "/stamp-boards")
					.param("isInProgress", "true")
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + ACCESS_TOKEN)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("stamp/boards-get-success",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
				),
				queryParameters(
					parameterWithName("memberId").description("조회할 member ID").optional(),
					parameterWithName("isInProgress").description("진행 중인 도장판 여부(boolean)")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("messages").description("응답 메시지"),
					fieldWithPath("data[]").description("응답 데이터"),
					fieldWithPath("data[].partner").description("상대 member ID").optional(),
					fieldWithPath("data[].partner.memberId").description("상대 member ID").optional(),
					fieldWithPath("data[].partner.nickname").description("상대 nickname").optional(),
					fieldWithPath("data[].partner.memberType").description("상대 member type").optional(),
					fieldWithPath("data[].partner.profileUrl").description("상대 member profile image url").optional(),
					fieldWithPath("data[].partner.kid").description("kid 여부").optional(),
					fieldWithPath("data[].stampBoardSummaries[]").description("도장판 목록").optional(),
					fieldWithPath("data[].stampBoardSummaries[].stampBoardId").description("도장판 ID").optional(),
					fieldWithPath("data[].stampBoardSummaries[].name").description("도장판 이름").optional(),
					fieldWithPath("data[].stampBoardSummaries[].currentStampCount").description("현재 모은 도장 개수")
						.optional(),
					fieldWithPath("data[].stampBoardSummaries[].goalStampCount").description("목표 도장 개수").optional(),
					fieldWithPath("data[].stampBoardSummaries[].reward").description("보상 쿠폰").optional(),
					fieldWithPath("data[].stampBoardSummaries[].missionCompleteCount").description("미션 완료 요청 개수")
						.optional(),
					fieldWithPath("data[].stampBoardSummaries[].status").description("도장판 상태").optional()
				)));
	}

	@Test
	@DisplayName("도장판 상세 조회 테스트")
	void getStampBoardTest() throws Exception {
		when(stampBoardService.getStampBoardDto(any(), anyLong())).thenReturn(STAMP_BOARD_DTO);

		mockMvc.perform(
				get(BASE_URL + "/stamp-board/{stampBoardId}", STAMP_BOARD_ID)
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + ACCESS_TOKEN)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("stamp/board-get-success",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
				),
				pathParameters(
					parameterWithName("stampBoardId").description("도장판 ID")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("messages").description("응답 메시지"),
					fieldWithPath("data").description("응답 데이터"),
					fieldWithPath("data.stampBoardId").description("도장판 ID"),
					fieldWithPath("data.name").description("도장판 이름"),
					fieldWithPath("data.status").description("도장판 상태"),
					fieldWithPath("data.currentStampCount").description("모은 도장 개수"),
					fieldWithPath("data.goalStampCount").description("목표 도장 개수"),
					fieldWithPath("data.reward").description("보상 쿠폰"),
					fieldWithPath("data.missions[]").description("미션 목록"),
					fieldWithPath("data.missions[].id").description("미션 ID"),
					fieldWithPath("data.missions[].content").description("미션 내용"),
					fieldWithPath("data.stamps[]").description("도장 목록").optional(),
					fieldWithPath("data.stamps[].id").description("도장 ID").optional(),
					fieldWithPath("data.stamps[].stampDesignId").description("도장 디자인 ID").optional(),
					fieldWithPath("data.stamps[].missionContent").description("미션 내용").optional(),
					fieldWithPath("data.stamps[].createdDate").description("도장 생성 시각").optional(),
					fieldWithPath("data.missionCompleteList[]").description("미션 완료 목록").optional(),
					fieldWithPath("data.missionCompleteList[].id").description("미션 완료 ID").optional(),
					fieldWithPath("data.missionCompleteList[].missionContent").description("미션 내용").optional(),
					fieldWithPath("data.missionCompleteList[].createdDate").description("미션 완료 시각").optional(),
					fieldWithPath("data.completedDate").description("도장 다 모은 시각"),
					fieldWithPath("data.rewardDate").description("쿠폰 수령 시각"),
					fieldWithPath("data.createdDate").description("도장판 생성 시각")
				)));
	}

	@Test
	@DisplayName("도장판 삭제 테스트")
	void deleteStampBoardTest() throws Exception {
		doNothing().when(stampBoardService).deleteStampBoard(any(), anyLong());

		mockMvc.perform(
				delete(BASE_URL + "/stamp-board/{stampBoardId}", STAMP_BOARD_ID)
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + ACCESS_TOKEN)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isNoContent())
			.andDo(document("stamp/board-delete-success",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
				),
				pathParameters(
					parameterWithName("stampBoardId").description("도장판 ID")
				)));
	}

	@Test
	@DisplayName("도장판 수정 테스트")
	void updateStampBoardTest() throws Exception {
		when(stampBoardService.updateStampBoard(any(), anyLong(), any())).thenReturn(STAMP_BOARD_DTO);

		mockMvc.perform(
				put(BASE_URL + "/stamp-board/{stampBoardId}", STAMP_BOARD_ID)
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + ACCESS_TOKEN)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectToString(STAMP_BOARD_UPDATE_REQUEST)))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("stamp/board-update-success",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
				),
				pathParameters(
					parameterWithName("stampBoardId").description("도장판 ID")
				),
				requestFields(
					fieldWithPath("name").description("도장판 이름(변경 없을 경우 기존 값 입력)"),
					fieldWithPath("reward").description("보상 쿠폰(변경 없을 경우 기존 값 입력)"),
					fieldWithPath("missions[].id").description("적용할 미션 ID(null일 경우 새 미션으로 처리)").optional(),
					fieldWithPath("missions[].content").description("적용할 미션 내용")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("messages").description("응답 메시지"),
					fieldWithPath("data").description("응답 데이터"),
					fieldWithPath("data.stampBoardId").description("도장판 ID"),
					fieldWithPath("data.name").description("도장판 이름"),
					fieldWithPath("data.status").description("도장판 상태"),
					fieldWithPath("data.currentStampCount").description("모은 도장 개수"),
					fieldWithPath("data.goalStampCount").description("목표 도장 개수"),
					fieldWithPath("data.reward").description("보상 쿠폰"),
					fieldWithPath("data.missions[]").description("미션 목록"),
					fieldWithPath("data.missions[].id").description("미션 ID"),
					fieldWithPath("data.missions[].content").description("미션 내용"),
					fieldWithPath("data.stamps[]").description("도장 목록").optional(),
					fieldWithPath("data.stamps[].id").description("도장 ID").optional(),
					fieldWithPath("data.stamps[].stampDesignId").description("도장 디자인 ID").optional(),
					fieldWithPath("data.stamps[].missionContent").description("미션 내용").optional(),
					fieldWithPath("data.stamps[].createdDate").description("도장 생성 시각").optional(),
					fieldWithPath("data.missionCompleteList[]").description("미션 완료 목록").optional(),
					fieldWithPath("data.missionCompleteList[].id").description("미션 완료 ID").optional(),
					fieldWithPath("data.missionCompleteList[].missionContent").description("미션 내용").optional(),
					fieldWithPath("data.missionCompleteList[].createdDate").description("미션 완료 시각").optional(),
					fieldWithPath("data.completedDate").description("도장 다 모은 시각"),
					fieldWithPath("data.rewardDate").description("쿠폰 수령 시각"),
					fieldWithPath("data.createdDate").description("도장판 생성 시각")
				)));
	}

	@Test
	@DisplayName("도장 생성 테스트")
	void createStampTest() throws Exception {
		doNothing().when(stampService).createStamp(any(), anyLong(), any());

		mockMvc.perform(
				post(BASE_URL + "/stamp-board/{stampBoardId}/stamp", STAMP_BOARD_ID)
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + ACCESS_TOKEN)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectToString(STAMP_CREATE_REQUEST)))
			.andDo(print())
			.andExpect(status().isCreated())
			.andDo(document("stamp/create-success",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
				),
				requestFields(
					fieldWithPath("count").description("추가할 도장 개수"),
					fieldWithPath("missionId").description("미션 ID"),
					fieldWithPath("stampDesignId").description("도장 디자인 ID")
				),
				pathParameters(
					parameterWithName("stampBoardId").description("도장판 ID")
				)));
	}

	@Test
	@DisplayName("도장 상세 조회 테스트")
	void getStampTest() throws Exception {
		when(stampService.getStampDto(anyLong(), anyLong())).thenReturn(STAMP_DTO);

		mockMvc.perform(
				get(BASE_URL + "/stamp-board/{stampBoardId}/{stampId}", STAMP_BOARD_ID, STAMP_ID)
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + ACCESS_TOKEN)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("stamp/get-success",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
				),
				pathParameters(
					parameterWithName("stampBoardId").description("도장판 ID"),
					parameterWithName("stampId").description("도장 ID")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("messages").description("응답 메시지"),
					fieldWithPath("data.id").description("도장 ID"),
					fieldWithPath("data.stampDesignId").description("도장 디자인 ID"),
					fieldWithPath("data.missionContent").description("미션 내용"),
					fieldWithPath("data.createdDate").description("생성 시각")
				)));
	}
}
