package com.polzzak.domain.mission.controller;

import static com.polzzak.support.MissionFixtures.*;
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

import com.polzzak.domain.stampboard.controller.MissionController;
import com.polzzak.domain.stampboard.service.StampBoardService;
import com.polzzak.domain.user.service.UserService;
import com.polzzak.support.test.ControllerTestHelper;

@WebMvcTest(MissionController.class)
class MissionControllerTest extends ControllerTestHelper {

	@MockBean
	private UserService userService;
	@MockBean
	private StampBoardService stampBoardService;

	private static final String BASE_URL = "/api/v1/missions";

	@BeforeEach
	public void setup() {
		when(userService.getMemberInfo(anyString())).thenReturn(KID);
		when(stampBoardService.getStampBoard(anyLong())).thenReturn(STAMP_BOARD);
		when(stampBoardService.getMission(anyLong())).thenReturn(MISSION);
		doNothing().when(stampBoardService).createMission(any(), any());
	}

	@Test
	@DisplayName("미션 완료 요청 생성 테스트")
	void createStampBoardTest() throws Exception {
		mockMvc.perform(
				post(BASE_URL + "/complete")
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + ACCESS_TOKEN)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectToString(MISSION_COMPLETE_CREATE_REQUEST)))
			.andDo(print())
			.andExpect(status().isCreated())
			.andDo(document("mission/complete-create-success",
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
