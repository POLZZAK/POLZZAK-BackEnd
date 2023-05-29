package com.polzzak.domain.stampboard.dto;

import com.polzzak.domain.stampboard.entity.StampBoard;

public record StampBoardSummary(long stampBoardId, String name, int currentStampCount, int goalStampCount,
								String reward, int missionCompleteCount, String status) {

	public static StampBoardSummary from(StampBoard stampBoard) {
		return new StampBoardSummary(stampBoard.getId(), stampBoard.getName(), stampBoard.getCurrentStampCount(),
			stampBoard.getGoalStampCount(), stampBoard.getReward(), stampBoard.getMissionCompletes().size(),
			StampBoard.Status.getLowerCase(stampBoard.getStatus()));
	}
}
