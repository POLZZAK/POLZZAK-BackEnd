package com.polzzak.application.dto;

import com.polzzak.domain.user.MemberType;
import com.polzzak.domain.user.SocialType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;


public record RegisterRequest(
    @NotBlank String username,
    @NotNull SocialType socialType,
    @NotNull MemberType memberType,
    @NotBlank @Length(min = 2, max = 10) String nickname
) {
}
