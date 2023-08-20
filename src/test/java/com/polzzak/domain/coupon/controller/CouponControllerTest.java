package com.polzzak.domain.coupon.controller;

import static com.polzzak.support.CouponFixtures.*;
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

import com.polzzak.domain.coupon.service.CouponService;
import com.polzzak.domain.user.service.UserService;
import com.polzzak.support.test.ControllerTestHelper;

@WebMvcTest(CouponController.class)
class CouponControllerTest extends ControllerTestHelper {

	@MockBean
	private CouponService couponService;
	@MockBean
	private UserService userService;

	private static final String BASE_URL = "/api/v1/coupons";

	@BeforeEach
	public void setup() {
		when(userService.getMemberInfo(anyString())).thenReturn(GUARDIAN);
		when(userService.getKidInfo(anyString())).thenReturn(KID);
	}

	@Test
	@DisplayName("쿠폰 발급 테스트")
	void issueCouponTest() throws Exception {
		doNothing().when(couponService).issueCoupon(any(), anyLong());

		mockMvc.perform(
				post(BASE_URL)
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + USER_ACCESS_TOKEN)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectToString(STAMP_BOARD_FOR_ISSUE_COUPON)))
			.andDo(print())
			.andExpect(status().isCreated())
			.andDo(document("coupon/coupon-create-success",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
				),
				requestFields(
					fieldWithPath("stampBoardId").description("쿠폰을 생성할 도장판 ID")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("messages").description("응답 메시지").optional(),
					fieldWithPath("data").description("응답 데이터").optional()
				)));
	}

	@Test
	@DisplayName("쿠폰 목록 조회 테스트")
	void getCouponsTest() throws Exception {
		when(couponService.getCouponList(any(), anyLong(), any())).thenReturn(COUPON_LIST_DTO_LIST);

		mockMvc.perform(
				get(BASE_URL)
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + USER_ACCESS_TOKEN)
					.contentType(MediaType.APPLICATION_JSON)
					.param("partnerMemberId", "123")
					.param("couponState", "issued")
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("coupon/coupons-get-success",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
				),
				queryParameters(
					parameterWithName("couponState").description("쿠폰 상태(선물 전, 받기 완료)"),
					parameterWithName("partnerMemberId").description("조회할 상대 member ID")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("messages").description("응답 메시지"),
					fieldWithPath("data[]").description("응답 데이터"),
					fieldWithPath("data[].family").description("상대방 정보").optional(),
					fieldWithPath("data[].family.memberId").description("상대방 정보").optional(),
					fieldWithPath("data[].family.nickname").description("상대방 이름").optional(),
					fieldWithPath("data[].family.memberType.name").description("상대방 멤버 타입 종류").optional(),
					fieldWithPath("data[].family.memberType.detail").description("상대방 멤버 타입 상세 정보").optional(),
					fieldWithPath("data[].family.profileUrl").description("상대방 프로필 URL").optional(),
					fieldWithPath("data[].coupons[]").description("상대방 프로필 URL").optional(),
					fieldWithPath("data[].coupons[].couponId").description("쿠폰 ID").optional(),
					fieldWithPath("data[].coupons[].reward").description("선물 내용").optional(),
					fieldWithPath("data[].coupons[].rewardRequestDate").description("조르기 시도한 시간").optional(),
					fieldWithPath("data[].coupons[].rewardDate").description("선물 주는 날짜").optional()
				)));
	}

	@Test
	@DisplayName("쿠폰 상세 조회 테스트")
	void getCouponTest() throws Exception {
		when(couponService.getCoupon(any(), anyLong())).thenReturn(COUPON_DTO);

		mockMvc.perform(
				get(BASE_URL + "/{couponId}", 1)
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + USER_ACCESS_TOKEN)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("coupon/coupon-get-success",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
				),
				pathParameters(
					parameterWithName("couponId").description("쿠폰 ID")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("messages").description("응답 메시지"),
					fieldWithPath("data").description("응답 데이터"),
					fieldWithPath("data.couponId").description("쿠폰 ID"),
					fieldWithPath("data.reward").description("보상"),
					fieldWithPath("data.guardian.nickname").description("보호자 이름"),
					fieldWithPath("data.guardian.profileUrl").description("보호자 프로필 이미지"),
					fieldWithPath("data.kid.nickname").description("아이 이름"),
					fieldWithPath("data.kid.profileUrl").description("아이 프로필 이미지"),
					fieldWithPath("data.missionContents[]").description("완료 미션 리스트"),
					fieldWithPath("data.stampCount").description("도장 개수"),
					fieldWithPath("data.state").description("쿠폰 상태"),
					fieldWithPath("data.rewardDate").description("상품 수령 날짜").optional(),
					fieldWithPath("data.rewardRequestDate").description("조르기 시도한 시간").optional(),
					fieldWithPath("data.startDate").description("도장판 시작 날짜"),
					fieldWithPath("data.endDate").description("도장판 완료 날짜")
				)));
	}

	@Test
	@DisplayName("보상 수령 테스트")
	void receiveCouponTest() throws Exception {
		doNothing().when(couponService).receiveReward(any(), anyLong());

		mockMvc.perform(
				post(BASE_URL + "/{couponId}/receive", 1)
					.header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + USER_ACCESS_TOKEN)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isNoContent())
			.andDo(document("coupon/coupon-receive-success",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
				),
				pathParameters(
					parameterWithName("couponId").description("쿠폰 ID")
				)));
	}
}
