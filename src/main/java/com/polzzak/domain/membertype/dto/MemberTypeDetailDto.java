package com.polzzak.domain.membertype.dto;

import com.polzzak.domain.membertype.entity.MemberTypeDetail;

public record MemberTypeDetailDto(
	Long memberTypeDetailId,
	String detail
) {
	public static MemberTypeDetailDto from(final MemberTypeDetail memberTypeDetail) {
		return new MemberTypeDetailDto(memberTypeDetail.getId(), memberTypeDetail.getDetail());
	}
}
