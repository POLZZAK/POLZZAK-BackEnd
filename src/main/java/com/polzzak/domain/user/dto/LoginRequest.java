package com.polzzak.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
	@NotBlank String authenticationCode,
	@NotBlank String redirectUri
) {
}
