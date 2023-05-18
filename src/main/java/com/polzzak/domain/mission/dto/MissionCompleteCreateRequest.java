package com.polzzak.domain.mission.dto;

import jakarta.annotation.Nonnull;

public record MissionCompleteCreateRequest(@Nonnull Long stampBoardId, @Nonnull Long missionId,
										   @Nonnull Long guardianId) {
}
