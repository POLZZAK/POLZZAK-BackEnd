package com.polzzak.support;

import static com.polzzak.support.UserFixtures.*;

import com.polzzak.domain.stampboard.dto.MissionRequestCreateRequest;
import com.polzzak.domain.stampboard.entity.Mission;
import com.polzzak.domain.stampboard.entity.StampBoard;
import com.polzzak.domain.user.dto.MemberDto;

public class MissionFixtures {
	public static final MissionRequestCreateRequest MISSION_COMPLETE_CREATE_REQUEST = new MissionRequestCreateRequest(
		1L, 22L, 4L);
	public static final MemberDto KID = new MemberDto(1L, "보호자", MEMBER_GUARDIAN_TYPE_DTO);
	public static final StampBoard STAMP_BOARD = new StampBoard(1L, 12L, "테스트 도장판", 30, "상이다");
	public static final Mission MISSION = new Mission(STAMP_BOARD, "미션");
}
