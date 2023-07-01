package com.polzzak.support;

import java.time.LocalDateTime;
import java.util.List;

import com.polzzak.domain.memberpoint.dto.MemberPointHistoryResponse;
import com.polzzak.domain.memberpoint.dto.MemberPointHistorySliceResponse;
import com.polzzak.domain.memberpoint.dto.MemberPointResponse;
import com.polzzak.domain.memberpoint.entity.MemberPointType;
import com.polzzak.domain.user.dto.MemberPointDto;

public class MemberPointFixtures {
	public static final MemberPointDto TEST_MEMBER_POINT_DTO;
	public static final MemberPointHistorySliceResponse TEST_MEMBER_POINT_HISTORY_SLICE_RESPONSE;
	public static final MemberPointHistoryResponse TEST_MEMBER_POINT_HISTORY_RESPONSE;
	public static final MemberPointResponse TEST_MEMBER_POINT_RESPONSE;
	public static final int TEST_DEFAULT_POINT = 70;
	public static final int TEST_DEFAULT_LEVEL = 0;

	static {
		TEST_MEMBER_POINT_DTO = new MemberPointDto(TEST_DEFAULT_POINT, TEST_DEFAULT_LEVEL);
		TEST_MEMBER_POINT_HISTORY_RESPONSE = new MemberPointHistoryResponse(
			MemberPointType.FAMILY_MAP_CREATION.getDescription(),
			MemberPointType.FAMILY_MAP_CREATION.getIncreasedPoint(),
			70, LocalDateTime.now());
		TEST_MEMBER_POINT_HISTORY_SLICE_RESPONSE = new MemberPointHistorySliceResponse(
			2L, List.of(TEST_MEMBER_POINT_HISTORY_RESPONSE));
		TEST_MEMBER_POINT_RESPONSE = new MemberPointResponse(TEST_DEFAULT_POINT, TEST_DEFAULT_LEVEL);
	}
}
