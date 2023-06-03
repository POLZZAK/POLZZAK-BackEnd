package com.polzzak.domain.user.dto;

import org.hibernate.validator.constraints.Length;

import com.polzzak.domain.user.entity.SocialType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
	@NotBlank String username,
	@NotNull SocialType socialType,
	@NotNull Long memberTypeDetailId,
	@NotBlank @Length(min = 2, max = 10) String nickname
) {
}
