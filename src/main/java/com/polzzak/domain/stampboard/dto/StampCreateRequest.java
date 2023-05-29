package com.polzzak.domain.stampboard.dto;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Min;

public record StampCreateRequest(@Min(1) int count, @Nonnull Long missionId, @Nonnull Integer stampDesignId) {
}
