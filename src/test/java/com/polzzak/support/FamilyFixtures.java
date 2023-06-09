package com.polzzak.support;

import static com.polzzak.support.UserFixtures.*;

import java.util.List;

import com.polzzak.domain.family.dto.FamilyMapRequest;
import com.polzzak.domain.family.dto.FamilyMemberDto;
import com.polzzak.domain.family.dto.FamilyMemberTypeDto;
import com.polzzak.domain.family.dto.FamilyNewRequestMarkDto;
import com.polzzak.domain.family.dto.FamilyStatus;
import com.polzzak.domain.family.dto.SearchedMemberDto;
import com.polzzak.domain.membertype.entity.MemberType;

public class FamilyFixtures {
	public static final int FAMILY_DEFAULT_COUNT = 1;
	public static final FamilyMapRequest TEST_FAMILY_MAP_REQUEST = new FamilyMapRequest(TEST_TARGET_MEMBER_ID);
	public static final FamilyMemberTypeDto FAMILY_MEMBER_KID_TYPE_DTO = new FamilyMemberTypeDto(MemberType.KID.name(),
		"아이");
	public static final FamilyMemberTypeDto FAMILY_MEMBER_GUARDIAN_TYPE_DTO = new FamilyMemberTypeDto(
		MemberType.GUARDIAN.name(),
		"엄마");
	public static final SearchedMemberDto SEARCHED_KID_MEMBER_DTO = new SearchedMemberDto(TEST_MEMBER_ID, TEST_NICKNAME,
		FAMILY_MEMBER_KID_TYPE_DTO, TEST_PROFILE_URL, FamilyStatus.NONE);
	public static final SearchedMemberDto SEARCHED_GUARDIAN_MEMBER_DTO = new SearchedMemberDto(TEST_MEMBER_ID,
		TEST_NICKNAME,
		FAMILY_MEMBER_GUARDIAN_TYPE_DTO, TEST_PROFILE_URL, FamilyStatus.NONE);
	public static final FamilyMemberDto FAMILY_KID_MEMBER_DTO = new FamilyMemberDto(TEST_MEMBER_ID, TEST_NICKNAME,
		FAMILY_MEMBER_KID_TYPE_DTO, TEST_PROFILE_URL);
	public static final FamilyMemberDto FAMILY_GUARDIAN_MEMBER_DTO = new FamilyMemberDto(TEST_MEMBER_ID, TEST_NICKNAME,
		FAMILY_MEMBER_GUARDIAN_TYPE_DTO, TEST_PROFILE_URL);
	public static final FamilyNewRequestMarkDto FAMILY_NEW_REQUEST_MARKER_DTO =
		new FamilyNewRequestMarkDto(true, false);
	public static final List<FamilyMemberDto> TEST_FAMILY_MEMBER_DTO_LIST = List.of(FAMILY_KID_MEMBER_DTO);
}
