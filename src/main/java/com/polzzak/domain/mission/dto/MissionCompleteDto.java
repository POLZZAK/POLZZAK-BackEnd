package com.polzzak.domain.mission.dto;

import java.time.LocalDateTime;

import com.polzzak.domain.mission.entity.MissionComplete;

public record MissionCompleteDto(long missionCompleteId, String content, LocalDateTime createdDate) {

	public static MissionCompleteDto from(MissionComplete missionComplete) {
		return new MissionCompleteDto(missionComplete.getId(), missionComplete.getMission().getContent(),
			missionComplete.getCreatedDate());
	}
}
