package com.polzzak.domain.stampboard.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polzzak.domain.family.dto.FamilyMemberDto;
import com.polzzak.domain.family.service.FamilyMapService;
import com.polzzak.domain.stampboard.dto.FamilyStampBoardSummary;
import com.polzzak.domain.stampboard.dto.MissionDto;
import com.polzzak.domain.stampboard.dto.MissionRequestCreateRequest;
import com.polzzak.domain.stampboard.dto.StampBoardCreateRequest;
import com.polzzak.domain.stampboard.dto.StampBoardDto;
import com.polzzak.domain.stampboard.dto.StampBoardGroup;
import com.polzzak.domain.stampboard.dto.StampBoardSummary;
import com.polzzak.domain.stampboard.dto.StampBoardUpdateRequest;
import com.polzzak.domain.stampboard.dto.StampCreateRequest;
import com.polzzak.domain.stampboard.dto.StampDto;
import com.polzzak.domain.stampboard.entity.Mission;
import com.polzzak.domain.stampboard.entity.MissionRequest;
import com.polzzak.domain.stampboard.entity.Stamp;
import com.polzzak.domain.stampboard.entity.StampBoard;
import com.polzzak.domain.stampboard.repository.MissionRepository;
import com.polzzak.domain.stampboard.repository.MissionRequestRepository;
import com.polzzak.domain.stampboard.repository.StampBoardRepository;
import com.polzzak.domain.stampboard.repository.StampRepository;
import com.polzzak.domain.user.dto.MemberDto;
import com.polzzak.domain.user.entity.Member;
import com.polzzak.domain.user.service.UserService;
import com.polzzak.global.exception.ErrorCode;
import com.polzzak.global.exception.PolzzakException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StampBoardService {

	private final UserService userService;
	private final FamilyMapService familyMapService;
	private final StampBoardRepository stampBoardRepository;
	private final MissionRepository missionRepository;
	private final MissionRequestRepository missionRequestRepository;
	private final StampRepository stampRepository;

	//StampBoard
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

		createMission(stampBoard, stampBoardCreateRequest.missionContents());
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

		updateMissions(stampBoard, stampBoard.getMissions(), stampBoardUpdateRequest.missions());

		return StampBoardDto.from(stampBoard);
	}

	@Transactional
	public void deleteStampBoard(MemberDto user, long stampBoardId) {
		StampBoard stampBoard = stampBoardRepository.getReferenceById(stampBoardId);
		if (stampBoard.isNotOwner(user.memberId())) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}

		stampBoardRepository.delete(stampBoard);
		deleteMissions(stampBoard.getId());
		deleteStamps(stampBoard.getId());
		deletemissionRequests(stampBoard.getId());
	}

	//Mission
	@Transactional
	public void createMission(StampBoard stampBoard, List<String> missionContents) {
		List<Mission> missions = missionContents.stream()
			.map(missionContent -> Mission.createMission()
				.stampBoard(stampBoard)
				.content(missionContent)
				.build())
			.toList();

		missionRepository.saveAll(missions);
	}

	public Mission getMission(long missionId) {
		return missionRepository.getReferenceById(missionId);
	}

	@Transactional
	public void createmissionRequest(MissionRequestCreateRequest missionRequestCreateRequest,
		MemberDto kid) {
		StampBoard stampBoard = getStampBoard(missionRequestCreateRequest.stampBoardId());

		validateForCreateMission(kid, stampBoard);

		Mission mission = getMission(missionRequestCreateRequest.missionId());
		Member guardianEntity = userService.findMemberByMemberId(missionRequestCreateRequest.guardianId());
		Member kidEntity = userService.findMemberByMemberId(kid.memberId());

		MissionRequest missionRequest = MissionRequest.createMissionRequest()
			.stampBoard(stampBoard)
			.mission(mission)
			.guardian(guardianEntity)
			.kid(kidEntity)
			.build();

		missionRequestRepository.save(missionRequest);
	}

	@Transactional
	public void updateMissions(StampBoard stampBoard, Collection<Mission> beforeMissions,
		List<MissionDto> afterMissionDtoList) {
		List<Long> activeMissionIds = afterMissionDtoList.stream()
			.map(MissionDto::id)
			.filter(Objects::nonNull)
			.toList();
		List<Long> inactiveMissionIds = beforeMissions.stream()
			.map(Mission::getId)
			.filter(id -> !activeMissionIds.contains(id))
			.toList();

		//before mission inactivate
		missionRepository.findByIdIn(inactiveMissionIds)
			.forEach(mission -> mission.changeActivate(false));

		List<Mission> newMissions = afterMissionDtoList.stream()
			.filter(missionDto -> missionDto.id() == null)
			.map(missionDto -> Mission.createMission()
				.stampBoard(stampBoard)
				.content(missionDto.content())
				.build())
			.toList();

		missionRepository.saveAll(newMissions);
	}

	@Transactional
	public void deleteMissions(long stampBoardId) {
		missionRepository.deleteByStampBoardId(stampBoardId);
	}

	@Transactional
	public void deletemissionRequests(long stampBoardId) {
		missionRequestRepository.deleteByStampBoardId(stampBoardId);
	}

	//Stamp
	@Transactional
	public void createStamp(MemberDto member, long stampBoardId, StampCreateRequest stampCreateRequest) {
		StampBoard stampBoard = getStampBoard(stampBoardId);
		validateForCreateStamp(stampBoard, member);
		int stampCount = getValidStampCountForAdd(stampBoard, stampCreateRequest.count());

		List<Stamp> stamps = new ArrayList<>(stampCount);

		Mission mission = getMission(stampCreateRequest.missionId());
		for (int i = 0; i < stampCount; i++) {
			stamps.add(Stamp.createMission()
				.stampBoard(stampBoard)
				.mission(mission)
				.stampDesignId(stampCreateRequest.stampDesignId())
				.build());
		}
		stampRepository.saveAll(stamps);

		stampBoard.addStampCount(stampCount);
	}

	public StampDto getStampDto(long stampBoardId, long stampId) {
		StampBoard stampBoard = getStampBoard(stampBoardId);
		if (!stampBoard.isValidStamp(stampId)) {
			throw new IllegalArgumentException("유효하지 않은 도장입니다.");
		}
		return StampDto.from(stampRepository.getReferenceById(stampId));
	}

	@Transactional
	public void deleteStamps(long stampBoardId) {
		stampRepository.deleteByStampBoardId(stampBoardId);
	}

	//StampBoard
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

	//Mission
	private void validateForCreateMission(MemberDto member, StampBoard stampBoard) {
		if (!member.isKid() || stampBoard.isNotOwner(member.memberId())) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}
		if (stampBoard.isCompleted()) {
			throw new IllegalArgumentException("이미 도장을 다 모았습니다.");
		}
	}

	//Stamp
	private void validateForCreateStamp(StampBoard stampBoard, MemberDto member) {
		if (stampBoard.isNotOwner(member.memberId())) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}
		if (stampBoard.isCompleted()) {
			throw new IllegalArgumentException("이미 도장을 다 모았습니다.");
		}
	}

	private int getValidStampCountForAdd(StampBoard stampBoard, int requestStampCount) {
		int remainingStampCount = stampBoard.getGoalStampCount() - stampBoard.getCurrentStampCount();
		return Math.min(requestStampCount, remainingStampCount);
	}
}
