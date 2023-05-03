package com.polzzak.auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OauthAccessTokenDto(
    @JsonProperty(value = "access_token")
    String accessToken
) {
}
