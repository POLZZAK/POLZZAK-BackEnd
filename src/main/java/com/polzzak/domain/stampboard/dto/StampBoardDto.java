package com.polzzak.domain.stampboard.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.polzzak.domain.membertype.dto.MemberTypeDetailDto;
import com.polzzak.domain.membertype.entity.MemberTypeDetail;
import com.polzzak.domain.notification.dto.MemberDtoForNotification;
import com.polzzak.domain.stampboard.entity.StampBoard;
import com.polzzak.domain.user.entity.Member;

public record StampBoardDto(
	long stampBoardId, String name, String status, MemberDtoForNotification kid, MemberDtoForNotification guardian,
	int currentStampCount, int goalStampCount, String reward, MemberTypeDetailDto guardianMemberType,
	List<MissionDto> missions, List<StampDto> stamps, List<MissionRequestDto> missionRequestList,
	LocalDateTime completedDate, LocalDateTime rewardDate, LocalDateTime createdDate
) {

	public static StampBoardDto from(StampBoard stampBoard, Member kid, Member guardian, String profileUrl) {
		MemberDtoForNotification kidDto = MemberDtoForNotification.from(kid, profileUrl);
		MemberDtoForNotification guardianDto = MemberDtoForNotification.from(guardian, null);
		MemberTypeDetailDto memberTypeDetailDto = MemberTypeDetailDto.from(guardian.getMemberType());
		List<MissionDto> missionDtoList = stampBoard.getMissions().stream()
			.map(MissionDto::from)
			.toList();
		List<StampDto> stampDtoList = stampBoard.getStamps().stream()
			.map(StampDto::from)
			.toList();
		List<MissionRequestDto> missionRequestDtoList = stampBoard.getMissionRequests().stream()
			.map(MissionRequestDto::from)
			.toList();

		return new StampBoardDto(stampBoard.getId(), stampBoard.getName(),
			StampBoard.Status.getLowerCase(stampBoard.getStatus()), kidDto, guardianDto,
			stampBoard.getCurrentStampCount(), stampBoard.getGoalStampCount(), stampBoard.getReward(),
			memberTypeDetailDto, missionDtoList, stampDtoList, missionRequestDtoList, stampBoard.getCompletedDate(),
			stampBoard.getRewardDate(), stampBoard.getCreatedDate());
	}
}
