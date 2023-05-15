package com.polzzak.domain.family.dto;

import lombok.NonNull;

public record FamilyMapRequest(
	@NonNull Long targetId
) {
}
