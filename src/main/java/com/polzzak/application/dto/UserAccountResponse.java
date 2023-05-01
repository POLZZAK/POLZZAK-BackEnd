package com.polzzak.application.dto;

import com.polzzak.domain.user.SocialType;

public record UserAccountResponse(
    String nickname,
    SocialType socialType
) {
}
