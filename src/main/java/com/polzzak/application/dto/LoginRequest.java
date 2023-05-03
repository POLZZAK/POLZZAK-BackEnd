package com.polzzak.application.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank String authenticationCode,
    @NotBlank String redirectUri
) {
}
