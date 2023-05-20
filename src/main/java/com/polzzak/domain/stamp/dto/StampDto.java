package com.polzzak.domain.stamp.dto;

import java.time.LocalDateTime;

import com.polzzak.domain.stamp.entity.Stamp;

public record StampDto(long id, int stampDesignId, String missionContent, LocalDateTime createdDate) {

	public static StampDto from(Stamp stamp) {
		return new StampDto(stamp.getId(), stamp.getStampDesignId(), stamp.getMission().getContent(),
			stamp.getCreatedDate());
	}
}
