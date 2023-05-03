package com.polzzak.user.model;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank String authenticationCode,
    @NotBlank String redirectUri
) {
}
