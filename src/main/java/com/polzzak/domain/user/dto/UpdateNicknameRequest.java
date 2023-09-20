package com.polzzak.domain.user.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

public record UpdateNicknameRequest(
	@NotBlank @Length(min = 2, max = 10) String nickname
) {
}
