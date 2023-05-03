package com.polzzak.application.dto;

import com.polzzak.domain.user.SocialType;

public record LoginResponse(
    String username,
    SocialType socialType
) {
}
