package com.polzzak.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OauthAccessTokenDto(
    @JsonProperty(value = "access_token")
    String accessToken
) {
}
