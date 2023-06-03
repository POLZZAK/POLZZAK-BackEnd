package com.polzzak.domain.family.dto;

import com.polzzak.domain.membertype.entity.MemberTypeDetail;

public record FamilyMemberTypeDto(
	String name,
	String detail
) {
	public static FamilyMemberTypeDto from(final MemberTypeDetail memberTypeDetail) {
		return new FamilyMemberTypeDto(memberTypeDetail.getType().name(), memberTypeDetail.getDetail());
	}
}
