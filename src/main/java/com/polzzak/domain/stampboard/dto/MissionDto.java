package com.polzzak.domain.stampboard.dto;

import com.polzzak.domain.stampboard.entity.Mission;

public record MissionDto(Long id, String content) {

	public static MissionDto from(Mission mission) {
		return new MissionDto(mission.getId(), mission.getContent());
	}
}
