package com.polzzak.domain.memberpoint.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberPointType {
	REGISTER("회원가입", 50),
	FAMILY_MAP_CREATION("연동 성공", 20),
	STAMP_BOARD_CREATION("도장판 생성", 5),
	STAMP_CREATION("도장 쾅", 10),
	STAMP_BOARD_REMOVAL("도장판 삭제", -20);
	private final String description;
	private final int increasedPoint;
}
