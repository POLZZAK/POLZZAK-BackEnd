package com.polzzak.domain.notification.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotificationType {
	FAMILY_REQUEST("\uD83D\uDC8C 연동 요청", "연동 요청", "%s님이 회원님께 연동 요청을 보냈어요", null),
	FAMILY_REQUEST_COMPLETE("\uD83E\uDD1D\uD83C\uDFFB 연동 완료", "연동 완료", "%s님과 연동이 완료되었어요! 도장판을 만들러 가볼까요? :)", "home"),
	FAMILY_REQUEST_REJECT("연동 실패", null, null, null),
	LEVEL_UP("레벨 상승", "\uD83E\uDD73 레벨 UP", "폴짝! %s으로 올라갔어요!", "my-page"),
	LEVEL_DOWN("레벨 하락", "\uD83D\uDEA8 레벨 DOWN", "조심! %s으로 내려왔어요", "my-page"),
	STAMP_REQUEST("도장 요청", "✊\uD83C\uDFFB 도장 요청", "'%s' 도장판에 도장을 찍어주세요!", "stamp-board/%s"),
	REWARD_REQUEST("선물 조르기", "⚡️ 선물 조르기", "'%s' 선물을 얼른 받고 싶어요!", "coupon/%s"),
	STAMP_BOARD_COMPLETE("도장 모으기 완료", "✔️️ 도장판 채우기 완료", "'%s' 도장판에 도장이 다 모였어요. 선물 쿠폰을 발급해주세요!", "stamp-board/%s"),
	REWARDED("선물 받기 완료", "\uD83C\uDF81 선물 받기 완료", "'%s' 선물 받기 완료! 선물을 주셔서 감사합니다. ❤️", "coupon/%s"),
	REWARD_REQUEST_AGAIN("선물 날짜 임박", "⏱️️ 선물 약속 날짜 D-1", "잊지마세요! '%s' 선물을 주기로 한 날짜가 <b>하루</b> 남았어요",
		"coupon/%s"),
	REWARD_FAIL("선물 전달하지 않음", "☠️ 선물 약속 어김", "실망이에요.. '%s' 선물 약속을 어기셨어요", "coupon/%s"),
	CREATED_STAMP_BOARD("도장판 생성", "\uD83E\uDD41️️ 새로운 도장판 도착", "'%s' 도장판이 만들어졌어요. 미션 수행 시~작!", "stamp-board/%s"),
	ISSUED_COUPON("쿠폰 발급 완료", "\uD83C\uDF9F️️ 쿠폰 발급 완료", "'%s' 도장판에 선물 쿠폰이 도착했어요! 쿠폰을 받으러 가볼까요?", "stamp-board/%s"),
	REWARDED_REQUEST("선물 받기 완료 요청", "\uD83C\uDF81️️ 혹시 선물은 잘 받았나요?",
		"'%s' 선물은 실제로 전달 받았나요? 선물을 받았따면 쿠폰에서 '선물 받기 완료' 버튼을 꼭 눌러주세요! 누르지 않으면 보호자는 <b>100P</b>가 깎여요",
		"coupon/%s");

	private final String description;
	private final String title;
	private final String message;
	private final String link;

	public String getMessageWithParameter(final String parameter) {
		return String.format(this.message, "<b>" + parameter + "</b>");
	}

	public String getLinkWithParameter(final String parameter) {
		if (link == null) {
			return null;
		}
		return String.format(this.link, parameter);
	}
}
