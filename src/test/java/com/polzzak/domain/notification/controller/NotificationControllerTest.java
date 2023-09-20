package com.polzzak.domain.notification.controller;

import static com.polzzak.support.CouponFixtures.*;
import static com.polzzak.support.StampFixtures.GUARDIAN;
import static com.polzzak.support.TokenFixtures.*;
import static org.mockito.ArgumentMatchers.*;
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

import com.polzzak.domain.notification.service.NotificationService;
import com.polzzak.domain.user.service.UserService;
import com.polzzak.support.NotificationFixtures;
import com.polzzak.support.test.ControllerTestHelper;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest extends ControllerTestHelper {

	@MockBean
	private NotificationService notificationService;
	@MockBean
	private UserService userService;

	private static final String BASE_URL = "/api/v1/notifications";

	@BeforeEach
	public void setup() {
		when(userService.getMemberInfo(anyString())).thenReturn(GUARDIAN);
		when(userService.getKidInfo(anyString())).thenReturn(KID);
	}

	@Test
	@DisplayName("알림 목록 조회 테스트")
	void getNotificationsTest() throws Exception {
		when(notificationService.getNotificationsAndChangeStatus(anyLong(), anyInt(), anyLong())).thenReturn(
			NotificationFixtures.NOTIFICATION_RESPONSE);

		mockMvc.perform(
				get(BASE_URL)
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + USER_ACCESS_TOKEN)
					.contentType(MediaType.APPLICATION_JSON)
					.param("startId", "10")
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("notification/notifications-get-success",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
				),
				queryParameters(
					parameterWithName("startId").description("조회 시작하는 알림 ID(첫 조회 : null)")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("messages").description("응답 메시지"),
					fieldWithPath("data").description("응답 데이터"),
					fieldWithPath("data.startId").description("다음 조회 ID (null이면 끝)").optional(),
					fieldWithPath("data.notificationDtoList[]").description("알림 목록"),
					fieldWithPath("data.notificationDtoList[].id").description("알림 ID").optional(),
					fieldWithPath("data.notificationDtoList[].type").description("알림 타").optional(),
					fieldWithPath("data.notificationDtoList[].status").description("알림 상태").optional(),
					fieldWithPath("data.notificationDtoList[].title").description("알림 제목").optional(),
					fieldWithPath("data.notificationDtoList[].message").description("알림 내용").optional(),
					fieldWithPath("data.notificationDtoList[].sender").description("전송자 정보").optional(),
					fieldWithPath("data.notificationDtoList[].sender.id").description("전송자 ID").optional(),
					fieldWithPath("data.notificationDtoList[].sender.nickname").description("전송자 닉네임").optional(),
					fieldWithPath("data.notificationDtoList[].sender.profileUrl").description("전송자 이미지").optional(),
					fieldWithPath("data.notificationDtoList[].link").description("알림 링크").optional(),
					fieldWithPath("data.notificationDtoList[].requestFamilyId").description("연동 요청 ID").optional(),
					fieldWithPath("data.notificationDtoList[].createdDate").description("알림 생성 시간").optional()
				)));
	}

	@Test
	@DisplayName("알림 삭제 테스트")
	void deleteNotificationTest() throws Exception {
		doNothing().when(notificationService).deleteNotifications(anyList());

		mockMvc.perform(
				delete(BASE_URL)
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + USER_ACCESS_TOKEN)
					.contentType(MediaType.APPLICATION_JSON)
					.queryParam("notificationIds", "1,2,3"))
			.andDo(print())
			.andExpect(status().isNoContent())
			.andDo(document("notification/notifications-delete-success",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
				),
				queryParameters(
					parameterWithName("notificationIds").description("삭제 요청 알림 ID")
				)));
	}

	@Test
	@DisplayName("알림 설정 조회 테스트")
	void getNotificationSettings() throws Exception {
		when(notificationService.getNotificationSetting(anyLong())).thenReturn(
			NotificationFixtures.NOTIFICATION_SETTING_DTO);

		mockMvc.perform(
				get(BASE_URL + "/settings")
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + USER_ACCESS_TOKEN)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("notification/settings-get-success",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("messages").description("응답 메시지"),
					fieldWithPath("data").description("응답 데이터"),
					fieldWithPath("data.familyRequest").description("연동 알림").optional(),
					fieldWithPath("data.level").description("레벨 알림").optional(),
					fieldWithPath("data.stampRequest").description("도장 요청 알림").optional(),
					fieldWithPath("data.stampBoardComplete").description("도장판 완성 알림").optional(),
					fieldWithPath("data.rewardRequest").description("선물 조르기 알림").optional(),
					fieldWithPath("data.rewarded").description("선물 전달 확인 알림").optional(),
					fieldWithPath("data.rewardFail").description("선물 약속 미이행 알림").optional(),
					fieldWithPath("data.createdStampBoard").description("새로운 도장판 알림").optional(),
					fieldWithPath("data.issuedCoupon").description("쿠폰 발급 알림").optional()
				)));
	}

	@Test
	@DisplayName("알림 설정 수정 테스트")
	void updateNotificationSettings() throws Exception {
		doNothing().when(notificationService).updateNotificationSetting(anyLong(), any());

		mockMvc.perform(
				patch(BASE_URL + "/settings")
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + USER_ACCESS_TOKEN)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectToString(NotificationFixtures.UPDATE_NOTIFICATION_SETTING)))
			.andDo(print())
			.andExpect(status().isNoContent())
			.andDo(document("notification/settings-update-success",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
				),
				requestFields(
					fieldWithPath("familyRequest").description("연동 알림"),
					fieldWithPath("level").description("레벨 알림"),
					fieldWithPath("stampRequest").description("도장 요청 알림"),
					fieldWithPath("stampBoardComplete").description("도장판 완성 알림"),
					fieldWithPath("rewardRequest").description("선물 조르기 알림"),
					fieldWithPath("rewarded").description("선물 전달 확인 알림"),
					fieldWithPath("rewardFail").description("선물 약속 미이행 알림"),
					fieldWithPath("createdStampBoard").description("새로운 도장판 알림"),
					fieldWithPath("issuedCoupon").description("쿠폰 발급 알림")
				)));
	}
}
