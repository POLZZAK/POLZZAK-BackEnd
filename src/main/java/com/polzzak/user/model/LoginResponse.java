package com.polzzak.user.model;

import com.polzzak.user.model.SocialType;

public record LoginResponse(
    String username,
    SocialType socialType
) {
}
