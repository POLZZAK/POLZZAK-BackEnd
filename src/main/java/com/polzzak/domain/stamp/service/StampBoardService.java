package com.polzzak.domain.stamp.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polzzak.domain.family.dto.FamilyMemberDto;
import com.polzzak.domain.family.service.FamilyMapService;
import com.polzzak.domain.mission.service.MissionService;
import com.polzzak.domain.stamp.dto.FamilyStampBoardSummary;
import com.polzzak.domain.stamp.dto.StampBoardCreateRequest;
import com.polzzak.domain.stamp.dto.StampBoardDto;
import com.polzzak.domain.stamp.dto.StampBoardGroup;
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
		if (!familyMapService.isFamily(guardian.memberId(), stampBoardCreateRequest.kidId())) {
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

	public List<FamilyStampBoardSummary> getFamilyStampBoardSummaries(MemberDto member, Long partnerMemberId,
		StampBoardGroup stampBoardGroup) {
		List<FamilyMemberDto> families = familyMapService.getMyFamilies(member.memberId());
		List<FamilyMemberDto> filteredFamilies = getFilteredFamiliesByPartnerId(families, partnerMemberId);

		return filteredFamilies.stream()
			.map(family -> {
				List<StampBoardSummary> stampBoardSummaries = getStampBoardSummaries(member, family.memberId(),
					stampBoardGroup);
				return FamilyStampBoardSummary.from(family, stampBoardSummaries);
			})
			.toList();
	}

	public StampBoard getStampBoard(long stampBoardId) {
		return stampBoardRepository.getReferenceById(stampBoardId);
	}

	@Transactional
	public StampBoardDto updateStampBoard(MemberDto member, long stampBoardId,
		StampBoardUpdateRequest stampBoardUpdateRequest) {
		StampBoard stampBoard = getStampBoard(stampBoardId);
		validateStampBoardForUpdate(stampBoard, member);

		if (!stampBoard.getName().equals(stampBoardUpdateRequest.name())) {
			stampBoard.updateName(stampBoardUpdateRequest.name());
		}
		if (!stampBoard.getReward().equals(stampBoardUpdateRequest.reward())) {
			stampBoard.updateReward(stampBoardUpdateRequest.reward());
		}

		missionService.updateMissions(stampBoard, stampBoard.getMissions(), stampBoardUpdateRequest.missions());

		return StampBoardDto.from(stampBoard);
	}

	@Transactional
	public void deleteStampBoard(MemberDto user, long stampBoardId) {
		StampBoard stampBoard = stampBoardRepository.getReferenceById(stampBoardId);
		if (stampBoard.isNotOwner(user.memberId())) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}
		stampBoardRepository.delete(stampBoard);
	}

	private List<StampBoard> getStampBoards(long guardianId, long kidId) {
		return stampBoardRepository.findByGuardianIdAndKidId(guardianId, kidId);
	}

	private List<FamilyMemberDto> getFilteredFamiliesByPartnerId(List<FamilyMemberDto> families, Long partnerMemberId) {
		if (partnerMemberId == null) {
			return families;
		}
		return families.stream()
			.filter(family -> family.memberId() == partnerMemberId)
			.toList();
	}

	private List<StampBoardSummary> getStampBoardSummaries(MemberDto member, long partnerId,
		StampBoardGroup stampBoardGroup) {
		List<StampBoard> stampBoards;
		if (member.isKid()) {
			stampBoards = getStampBoards(partnerId, member.memberId());
		} else {
			stampBoards = getStampBoards(member.memberId(), partnerId);
		}

		return getFilteredStampBoardsByGroup(stampBoards, stampBoardGroup).stream()
			.map(StampBoardSummary::from)
			.toList();
	}

	private List<StampBoard> getFilteredStampBoardsByGroup(List<StampBoard> stampBoards,
		StampBoardGroup stampBoardGroup) {
		if (stampBoardGroup == StampBoardGroup.IN_PROGRESS) {
			return stampBoards.stream()
				.filter(stampBoard -> StampBoard.Status.getProgressStatuses().contains(stampBoard.getStatus()))
				.toList();
		}
		return stampBoards.stream()
			.filter(stampBoard -> stampBoard.getStatus() == StampBoard.Status.REWARDED)
			.toList();
	}

	private void validateStampBoardForUpdate(StampBoard stampBoard, MemberDto member) {
		if (stampBoard.isNotOwner(member.memberId())) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}
		if (stampBoard.getCurrentStampCount() == stampBoard.getGoalStampCount()) {
			throw new IllegalArgumentException("도장을 다 모으면 수정할 수 없습니다.");
		}
	}
}
