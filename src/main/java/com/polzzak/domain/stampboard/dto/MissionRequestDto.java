package com.polzzak.domain.stampboard.dto;

import java.time.LocalDateTime;

import com.polzzak.domain.stampboard.entity.MissionRequest;

public record MissionRequestDto(
	long id,
	String missionContent,
	LocalDateTime createdDate
) {

	public static MissionRequestDto from(MissionRequest missionRequest) {
		return new MissionRequestDto(missionRequest.getId(), missionRequest.getMission().getContent(),
			missionRequest.getCreatedDate());
	}
}
