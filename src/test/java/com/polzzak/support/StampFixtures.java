package com.polzzak.support;

import java.time.LocalDateTime;
import java.util.List;

import com.polzzak.domain.family.dto.FamilyMemberDto;
import com.polzzak.domain.stampboard.dto.FamilyStampBoardSummary;
import com.polzzak.domain.stampboard.dto.MissionCompleteDto;
import com.polzzak.domain.stampboard.dto.MissionDto;
import com.polzzak.domain.stampboard.dto.StampBoardCreateRequest;
import com.polzzak.domain.stampboard.dto.StampBoardDto;
import com.polzzak.domain.stampboard.dto.StampBoardSummary;
import com.polzzak.domain.stampboard.dto.StampBoardUpdateRequest;
import com.polzzak.domain.stampboard.dto.StampCreateRequest;
import com.polzzak.domain.stampboard.dto.StampDto;
import com.polzzak.domain.user.dto.MemberDto;
import com.polzzak.domain.user.entity.MemberType;

public class StampFixtures {
	public static final long STAMP_BOARD_ID = 33L;
	public static final long STAMP_ID = 5L;

	public static final MemberDto GUARDIAN = new MemberDto(1L, "보호자", MemberType.FATHER, "image_url");
	public static final StampBoardCreateRequest STAMP_BOARD_CREATE_REQUEST = new StampBoardCreateRequest(1L, "도장판 이름",
		30, "칭찬 상이다", List.of("미션1", "미션2", "미션3"));
	public static final FamilyMemberDto KID1 = new FamilyMemberDto(11L, "아이 1", MemberType.KID, "image_url");
	public static final FamilyMemberDto KID2 = new FamilyMemberDto(12L, "아이 2", MemberType.KID, "image_url");
	public static final List<StampBoardSummary> STAMP_BOARD_SUMMARIES1 = List.of(
		new StampBoardSummary(1L, "도장판 1", 13, 30, "콘서트", 5, "progress"),
		new StampBoardSummary(3L, "도장판 3", 30, 30, "칭찬", 0, "completed")
	);
	public static final List<FamilyStampBoardSummary> FAMILY_STAMP_BOARD_SUMMARIES = List.of(
		new FamilyStampBoardSummary(KID1, STAMP_BOARD_SUMMARIES1),
		new FamilyStampBoardSummary(KID2, List.of())
	);
	public static final List<MissionDto> MISSION_DTOS = List.of(
		new MissionDto(1L, "미션1"),
		new MissionDto(2L, "미션2")
	);
	public static final List<StampDto> STAMP_DTOS = List.of(
		new StampDto(11, 1, "미션1", LocalDateTime.now()),
		new StampDto(12, 3, "미션2", LocalDateTime.now())
	);
	public static final List<MissionCompleteDto> MISSION_COMPLETE_DTOS = List.of(
		new MissionCompleteDto(21, "미션3", LocalDateTime.now()),
		new MissionCompleteDto(22, "미션4", LocalDateTime.now())
	);
	public static final StampBoardDto STAMP_BOARD_DTO = new StampBoardDto(33L, "도장판 이름", "progress", 13, 30, "칭찬해주기",
		MISSION_DTOS, STAMP_DTOS, MISSION_COMPLETE_DTOS, null, null, LocalDateTime.now());
	public static final List<MissionDto> UPDATE_MISSION_DTOS = List.of(
		new MissionDto(2L, "지울 미션은"),
		new MissionDto(3L, "안 보내면 됩니다."),
		new MissionDto(null, "새 미션")
	);
	public static final StampBoardUpdateRequest STAMP_BOARD_UPDATE_REQUEST = new StampBoardUpdateRequest("도장판 이름",
		"칭찬 상이다", UPDATE_MISSION_DTOS);
	public static final StampCreateRequest STAMP_CREATE_REQUEST = new StampCreateRequest(4, 1L, 2);
	public static final StampDto STAMP_DTO = new StampDto(11, 1, "미션1", LocalDateTime.now());
}
