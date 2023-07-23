package com.polzzak.domain.stampboard.dto;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;

public record StampBoardUpdateRequest(
	@NotBlank @Length(min = 2, max = 20) String name,
	@NotBlank @Length(min = 1, max = 30) String reward,
	@Nonnull List<MissionDto> missions,
	@Nonnull Integer goalStampCount
) {
}
