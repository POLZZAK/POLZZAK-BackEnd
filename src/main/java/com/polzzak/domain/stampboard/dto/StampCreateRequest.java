package com.polzzak.domain.stampboard.dto;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Min;

public record StampCreateRequest(Long missionRequestId, Long missionId, @Nonnull Integer stampDesignId) {
}
