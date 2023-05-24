package com.polzzak.domain.stamp.dto;

public enum StampBoardGroup {

	IN_PROGRESS, ENDED;

	public static StampBoardGroup getStampBoardGroupByStr(String stampBoardGroupAsStr) {
		return valueOf(stampBoardGroupAsStr.toUpperCase());
	}
}
