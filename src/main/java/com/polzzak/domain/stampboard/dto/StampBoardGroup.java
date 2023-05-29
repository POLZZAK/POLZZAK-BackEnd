package com.polzzak.domain.stampboard.dto;

public enum StampBoardGroup {

	IN_PROGRESS, ENDED;

	public static StampBoardGroup getStampBoardGroupByStr(String stampBoardGroupAsStr) {
		return valueOf(stampBoardGroupAsStr.toUpperCase());
	}
}
