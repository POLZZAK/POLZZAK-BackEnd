package com.polzzak.domain.notification.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotificationType {
	FAMILY_REQUEST("연동 요청", "연동 요청", "%s님이 회원님께 연동 요청을 보냈어요", "https://www.google.com"),
	FAMILY_REQUEST_COMPLETE("연동완료", "연동 완료", "%s님과 연동이 완료되었어요! 도장판을 만들러 가볼까요? :)", "https://www.google.com"),
	LEVEL_UP("레벨 상승", "레벨 UP", "폴짝! %s으로 올라갔어요!", "https://www.google.com"),
	LEVEL_DOWN("레벨 하락", "레벨 DOWN", "조심! %s으로 내려왔어요", "https://www.google.com"),
	STAMP_REQUEST("도장 요청", "도장 요청", "'%s' 도장판에 도장을 찍어주세요!", "https://www.google.com"),
	REWARD_REQUEST("선물 조르기", "선물 조르기", "'%s' 선물을 얼른 받고 싶어요!", "https://www.google.com"),
	STAMP_BOARD_COMPLETE("도장 모으기 완료", "도장판 채우기 완료", "'%s' 도장판에 도장이 다 모였어요. 선물 쿠폰을 발급해주세요!", "https://www.google.com"),
	REWARDED("선물 받기 완료", "선물 받기 완료", "'%s' 선물 받기 완료! 선물을 주셔서 감사합니다.", "https://www.google.com"),
	REWARD_REQUEST_AGAIN("선물 날짜 임박", "선물 약속 날짜 D-1", "잊지마세요! '%s' 선물을 주기로 한 날짜가 <b>하루</b> 남았어요",
		"https://www.google.com"),
	REWARD_FAIL("선물 전달하지 않음", "선물 약속 어김", "실망이에요.. '%s' 선물 약속을 어기셨어요", "https://www.google.com"),
	CREATED_STAMP_BOARD("도장판 생성", "새로운 도장판 도착", "'%s' 도장판이 만들어졌어요. 미션 수행 시~작!", "https://www.google.com"),
	ISSUED_COUPON("쿠폰 발급 완료", "쿠폰 발급 완료", "'%s' 도장판에 선물 쿠폰이 도착했어요! 쿠폰을 받으러 가볼까요?", "https://www.google.com"),
	REWARDED_REQUEST("선물 받기 완료 요청", "혹시 선물은 잘 받았나요?",
		"'%s' 선물은 실제로 전달 받았나요? 선물을 받았따면 쿠폰에서 '선물 받기 완료' 버튼을 꼭 눌러주세요! 누르지 않으면 보호자는 <b>100P</b>가 깎여요",
		"https://www.google.com");

	private final String description;
	private final String title;
	private final String message;
	private final String link;

	public String getMessageWithParameter(final String parameter) {
		return String.format(this.message, "<b>" + parameter + "</b>");
	}
}
