package com.polzzak.domain.membertype.dto;

import java.util.List;

public record MemberTypeDetailListDto(
	List<MemberTypeDetailDto> memberTypeDetailList
) {
	public static MemberTypeDetailListDto from(final List<MemberTypeDetailDto> memberTypeDetailDtos) {
		return new MemberTypeDetailListDto(memberTypeDetailDtos);
	}
}
