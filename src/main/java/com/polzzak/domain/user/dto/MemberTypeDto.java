package com.polzzak.domain.user.dto;

import com.polzzak.domain.membertype.entity.MemberTypeDetail;

public record MemberTypeDto(
	String name,
	String detail
) {
	public static MemberTypeDto from(final MemberTypeDetail memberTypeDetail) {
		return new MemberTypeDto(memberTypeDetail.getType().name(), memberTypeDetail.getDetail());
	}
}
