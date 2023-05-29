package com.polzzak.domain.stampboard.dto;

import jakarta.annotation.Nonnull;

public record MissionRequestCreateRequest(
	@Nonnull Long stampBoardId,
	@Nonnull Long missionId,
	@Nonnull Long guardianId
) {
}
