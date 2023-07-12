package com.polzzak.domain.user.dto;

import com.polzzak.domain.membertype.entity.MemberType;
import com.polzzak.domain.membertype.entity.MemberTypeDetail;

public record MemberTypeDto(
	String name,
	String detail
) {
	public static MemberTypeDto from(final MemberTypeDetail memberTypeDetail) {
		return new MemberTypeDto(memberTypeDetail.getType().name(), memberTypeDetail.getDetail());
	}

	public static boolean isKid(MemberTypeDto memberTypeDto) {
		return memberTypeDto.name.equals(MemberType.KID.name());
	}
}
