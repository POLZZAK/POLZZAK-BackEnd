package com.polzzak.domain.stampboard.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.polzzak.domain.stampboard.entity.StampBoard;

public record StampBoardDto(long stampBoardId, String name, String status, int currentStampCount, int goalStampCount,
							String reward, List<MissionDto> missions, List<StampDto> stamps,
							List<MissionRequestDto> missionRequestList, LocalDateTime completedDate,
							LocalDateTime rewardDate, LocalDateTime createdDate) {

	public static StampBoardDto from(StampBoard stampBoard) {
		List<MissionDto> missionDtoList = stampBoard.getMissions().stream()
			.map(MissionDto::from)
			.toList();
		List<StampDto> stampDtoList = stampBoard.getStamps().stream()
			.map(StampDto::from)
			.toList();
		List<MissionRequestDto> missionRequestDtoList = stampBoard.getMissionRequests().stream()
			.map(MissionRequestDto::from)
			.toList();

		return new StampBoardDto(stampBoard.getId(), stampBoard.getName(),
			StampBoard.Status.getLowerCase(stampBoard.getStatus()), stampBoard.getCurrentStampCount(),
			stampBoard.getGoalStampCount(), stampBoard.getReward(), missionDtoList, stampDtoList,
			missionRequestDtoList, stampBoard.getCompletedDate(), stampBoard.getRewardDate(),
			stampBoard.getCreatedDate());
	}
}
