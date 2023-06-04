package com.polzzak.support;

import java.util.List;

import com.polzzak.domain.membertype.dto.MemberTypeDetailDto;
import com.polzzak.domain.membertype.dto.MemberTypeDetailListDto;
import com.polzzak.domain.membertype.dto.MemberTypeDetailRequest;
import com.polzzak.domain.membertype.entity.MemberType;

public class MemberTypeFixtures {
	public static final Long TEST_DETAIL_ID = 1L;
	public static final Long TEST_NOT_EXIST_DETAIL_ID = 0L;
	public static final String NOT_EXIST_DETAIL = "noExistTestDetail";
	public static final String DUPLICATED_DETAIL = "duplicatedDetail";
	public static final MemberTypeDetailListDto MEMBER_TYPE_DETAIL_LIST_DTO = new MemberTypeDetailListDto(
		List.of(new MemberTypeDetailDto(1L, "엄마"), new MemberTypeDetailDto(2L, "아빠")));

	public static final MemberTypeDetailRequest MEMBER_TYPE_DETAIL_REQUEST = new MemberTypeDetailRequest(
		MemberType.GUARDIAN, "something");

	public static final MemberTypeDetailRequest DUPLICATED_MEMBER_TYPE_DETAIL_REQUEST = new MemberTypeDetailRequest(
		MemberType.GUARDIAN, DUPLICATED_DETAIL);
}
