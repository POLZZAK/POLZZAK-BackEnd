package com.polzzak.domain.stamp.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polzzak.domain.family.dto.FamilyMemberDto;
import com.polzzak.domain.family.service.FamilyMapService;
import com.polzzak.domain.mission.service.MissionService;
import com.polzzak.domain.stamp.dto.FamilyStampBoardSummary;
import com.polzzak.domain.stamp.dto.StampBoardCreateRequest;
import com.polzzak.domain.stamp.dto.StampBoardDto;
import com.polzzak.domain.stamp.dto.StampBoardSummary;
import com.polzzak.domain.stamp.dto.StampBoardUpdateRequest;
import com.polzzak.domain.stamp.entity.StampBoard;
import com.polzzak.domain.stamp.repository.StampBoardRepository;
import com.polzzak.domain.user.dto.MemberDto;
import com.polzzak.global.exception.ErrorCode;
import com.polzzak.global.exception.PolzzakException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StampBoardService {

	private final MissionService missionService;
	private final FamilyMapService familyMapService;
	private final StampBoardRepository stampBoardRepository;

	@Transactional
	public void createStampBoard(MemberDto guardian, StampBoardCreateRequest stampBoardCreateRequest) {
		if (!isFamily(guardian, stampBoardCreateRequest.kidId())) {
			throw new IllegalArgumentException("가족이 아닙니다.");
		}
		StampBoard stampBoard = stampBoardRepository.save(StampBoard.createStampBoard()
			.guardianId(guardian.memberId())
			.kidId(stampBoardCreateRequest.kidId())
			.name(stampBoardCreateRequest.name())
			.goalStampCount(stampBoardCreateRequest.goalStampCount())
			.reward(stampBoardCreateRequest.reward())
			.build());

		missionService.createMission(stampBoard, stampBoardCreateRequest.missionContents());
	}

	public StampBoardDto getStampBoardDto(MemberDto member, long stampBoardId) {
		StampBoard stampBoard = getStampBoard(stampBoardId);
		if (stampBoard.isNotOwner(member.memberId())) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}

		return StampBoardDto.from(stampBoard);
	}

	public List<FamilyStampBoardSummary> getFamilyStampBoardSummaries(MemberDto member, Long filterMemberId,
		boolean isInProgress) {
		List<FamilyMemberDto> families = familyMapService.getMyFamilies(member.memberId());
		List<FamilyMemberDto> filteredFamilies = getFilteredFamilies(families, filterMemberId);

		return filteredFamilies.stream()
			.map(family -> {
				List<StampBoardSummary> stampBoardSummaries = getStampBoardSummaries(member, family.memberId(),
					isInProgress);
				return FamilyStampBoardSummary.from(family, stampBoardSummaries);
			})
			.toList();
	}

	public StampBoard getStampBoard(long stampBoardId) {
		return stampBoardRepository.getReferenceById(stampBoardId);
	}

	@Transactional
	public void updateStampBoard(MemberDto member, long stampBoardId, StampBoardUpdateRequest stampBoardUpdateRequest) {
		StampBoard stampBoard = getStampBoard(stampBoardId);
		if (stampBoard.isNotOwner(member.memberId())) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}
		if (stampBoard.getCurrentStampCount() == stampBoard.getGoalStampCount()) {
			throw new IllegalArgumentException("도장을 다 모으면 수정할 수 없습니다.");
		}

		if (!stampBoard.getName().equals(stampBoardUpdateRequest.name())) {
			stampBoard.setName(stampBoardUpdateRequest.name());
		}
		if (!stampBoard.getReward().equals(stampBoardUpdateRequest.reward())) {
			stampBoard.setReward(stampBoardUpdateRequest.reward());
		}

		missionService.updateMissions(stampBoard, stampBoard.getMissions(), stampBoardUpdateRequest.missions());
	}

	private List<StampBoard> getStampBoards(long guardianId, long kidId) {
		return stampBoardRepository.findByGuardianIdAndKidId(guardianId, kidId);
	}

	@Transactional
	public void deleteStampBoard(MemberDto user, long stampBoardId) {
		StampBoard stampBoard = stampBoardRepository.getReferenceById(stampBoardId);
		if (stampBoard.isNotOwner(user.memberId())) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}
		stampBoardRepository.delete(stampBoard);
	}

	private boolean isFamily(MemberDto guardian, long kidId) {
		List<FamilyMemberDto> families = familyMapService.getMyFamilies(guardian.memberId());
		return families.stream()
			.anyMatch(family -> family.memberId() == kidId);
	}

	private List<FamilyMemberDto> getFilteredFamilies(List<FamilyMemberDto> families, Long filterMemberId) {
		if (filterMemberId == null) {
			return families;
		} else {
			return families.stream()
				.filter(family -> family.memberId() == filterMemberId)
				.toList();
		}
	}

	private List<StampBoardSummary> getStampBoardSummaries(MemberDto member, long partnerId, boolean isInProgress) {
		List<StampBoard> stampBoards;
		if (member.isKid()) {
			stampBoards = getStampBoards(partnerId, member.memberId());
		} else {
			stampBoards = getStampBoards(member.memberId(), partnerId);
		}

		List<StampBoard> filteredStampBoards = new ArrayList<>();
		if (isInProgress) {
			filteredStampBoards.addAll(stampBoards.stream()
				.filter(stampBoard -> StampBoard.Status.getProgressStatuses().contains(stampBoard.getStatus()))
				.toList());
		} else {
			filteredStampBoards.addAll(stampBoards.stream()
				.filter(stampBoard -> stampBoard.getStatus() == StampBoard.Status.REWARDED)
				.toList());
		}

		return filteredStampBoards.stream()
			.map(StampBoardSummary::from)
			.toList();
	}
}
