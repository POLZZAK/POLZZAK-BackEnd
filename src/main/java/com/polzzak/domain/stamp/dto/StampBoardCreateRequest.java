package com.polzzak.domain.stamp.dto;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;

public record StampBoardCreateRequest(@Nonnull Long kidId,
									  @NotBlank @Length(min = 2, max = 20) String name,
									  @Nonnull Integer goalStampCount,
									  @NotBlank @Length(min = 1, max = 30) String reward,
									  @Nonnull List<String> missionContents) {
}
