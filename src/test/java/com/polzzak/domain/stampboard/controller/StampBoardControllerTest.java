package com.polzzak.domain.stampboard.controller;

import static com.polzzak.support.MissionFixtures.*;
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

import com.polzzak.domain.stampboard.service.StampBoardService;
import com.polzzak.domain.user.service.UserService;
import com.polzzak.support.test.ControllerTestHelper;

@WebMvcTest(StampBoardController.class)
class StampBoardControllerTest extends ControllerTestHelper {

	@MockBean
	private StampBoardService stampBoardService;
	@MockBean
	private UserService userService;

	private static final String BASE_URL = "/api/v1/stamps";

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
		when(stampBoardService.getFamilyStampBoardSummaries(any(), isNull(), any())).thenReturn(
			FAMILY_STAMP_BOARD_SUMMARIES);

		mockMvc.perform(
				get(BASE_URL + "/stamp-boards")
					.param("stampBoardGroup", "in_progress")
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
					parameterWithName("stampBoardGroup").description("진행 중인 도장판 여부(in_progress, ended)")
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
					fieldWithPath("data[].stampBoardSummaries[]").description("도장판 목록").optional(),
					fieldWithPath("data[].stampBoardSummaries[].stampBoardId").description("도장판 ID").optional(),
					fieldWithPath("data[].stampBoardSummaries[].name").description("도장판 이름").optional(),
					fieldWithPath("data[].stampBoardSummaries[].currentStampCount").description("현재 모은 도장 개수")
						.optional(),
					fieldWithPath("data[].stampBoardSummaries[].goalStampCount").description("목표 도장 개수").optional(),
					fieldWithPath("data[].stampBoardSummaries[].reward").description("보상 쿠폰").optional(),
					fieldWithPath("data[].stampBoardSummaries[].missionRequestCount").description("미션 완료 요청 개수")
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
					fieldWithPath("data.missionRequestList[]").description("미션 완료 목록").optional(),
					fieldWithPath("data.missionRequestList[].id").description("미션 완료 ID").optional(),
					fieldWithPath("data.missionRequestList[].missionContent").description("미션 내용").optional(),
					fieldWithPath("data.missionRequestList[].createdDate").description("미션 완료 시각").optional(),
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
				patch(BASE_URL + "/stamp-board/{stampBoardId}", STAMP_BOARD_ID)
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
					fieldWithPath("data.missionRequestList[]").description("미션 완료 목록").optional(),
					fieldWithPath("data.missionRequestList[].id").description("미션 완료 ID").optional(),
					fieldWithPath("data.missionRequestList[].missionContent").description("미션 내용").optional(),
					fieldWithPath("data.missionRequestList[].createdDate").description("미션 완료 시각").optional(),
					fieldWithPath("data.completedDate").description("도장 다 모은 시각"),
					fieldWithPath("data.rewardDate").description("쿠폰 수령 시각"),
					fieldWithPath("data.createdDate").description("도장판 생성 시각")
				)));
	}

	@Test
	@DisplayName("도장 생성 테스트")
	void createStampTest() throws Exception {
		doNothing().when(stampBoardService).createStamp(any(), anyLong(), any());

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
		when(stampBoardService.getStampDto(anyLong(), anyLong())).thenReturn(STAMP_DTO);

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

	@Test
	@DisplayName("미션 완료 요청 생성 테스트")
	void createMissionRequestTest() throws Exception {
		when(userService.getMemberInfo(anyString())).thenReturn(KID);
		when(stampBoardService.getStampBoard(anyLong())).thenReturn(STAMP_BOARD);
		when(stampBoardService.getMission(anyLong())).thenReturn(MISSION);
		doNothing().when(stampBoardService).createMission(any(), any());

		mockMvc.perform(
				post(BASE_URL + "/mission-request")
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + ACCESS_TOKEN)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectToString(MISSION_COMPLETE_CREATE_REQUEST)))
			.andDo(print())
			.andExpect(status().isCreated())
			.andDo(document("mission/request-create-success",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
				),
				requestFields(
					fieldWithPath("stampBoardId").description("도장판 ID"),
					fieldWithPath("missionId").description("미션 ID"),
					fieldWithPath("guardianId").description("보호자 ID")
				)));
	}
}
