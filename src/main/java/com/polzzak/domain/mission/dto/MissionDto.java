package com.polzzak.domain.mission.dto;

import com.polzzak.domain.mission.entity.Mission;

public record MissionDto(Long id, String content) {

	public static MissionDto from(Mission mission) {
		return new MissionDto(mission.getId(), mission.getContent());
	}
}
