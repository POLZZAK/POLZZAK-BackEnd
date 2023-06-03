package com.polzzak.domain.membertype.dto;

import com.polzzak.domain.membertype.entity.MemberType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MemberTypeDetailRequest(
	@NotNull
	MemberType memberType,
	@NotBlank
	String detail
) {
}
