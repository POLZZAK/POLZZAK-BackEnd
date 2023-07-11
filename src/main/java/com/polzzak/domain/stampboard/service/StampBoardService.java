package com.polzzak.domain.stampboard.service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.context.ApplicationEventPublisher;
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
import com.polzzak.domain.stampboard.entity.StampBoardCreatedEvent;
import com.polzzak.domain.stampboard.entity.StampBoardDeletedEvent;
import com.polzzak.domain.stampboard.entity.StampCreatedEvent;
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
	private final ApplicationEventPublisher eventPublisher;

	//StampBoard
	@Transactional
	public void createStampBoard(final String username, final StampBoardCreateRequest stampBoardCreateRequest) {
		Member findMember = userService.findMemberByUsername(username);
		if (!familyMapService.isFamily(findMember.getId(), stampBoardCreateRequest.kidId())) {
			throw new IllegalArgumentException("가족이 아닙니다.");
		}
		StampBoard stampBoard = stampBoardRepository.save(createStampBoard(stampBoardCreateRequest, findMember));
		createMission(stampBoard, stampBoardCreateRequest.missionContents());
		eventPublisher.publishEvent(new StampBoardCreatedEvent(findMember));
	}

	public StampBoardDto getStampBoardDto(MemberDto member, long stampBoardId) {
		StampBoard stampBoard = getStampBoard(stampBoardId);
		if (stampBoard.isNotOwner(member.memberId())) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}

		return StampBoardDto.from(stampBoard);
	}

	public List<FamilyStampBoardSummary> getFamilyStampBoardSummaries(final String username, final Long partnerMemberId,
		final StampBoardGroup stampBoardGroup) {
		Member member = userService.findMemberByUsername(username);
		List<FamilyMemberDto> families = familyMapService.getMyFamilies(member.getId());
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
		StampBoard stampBoard = stampBoardRepository.findByIdAndIsDeleted(stampBoardId, false);
		if (stampBoard == null) {
			throw new IllegalArgumentException("stamp board is deleted.");
		}
		return stampBoard;
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
	public void deleteStampBoard(final String username, final long stampBoardId) {
		Member findMember = userService.findMemberByUsername(username);
		StampBoard stampBoard = stampBoardRepository.findByIdAndIsDeleted(stampBoardId, false);
		if (stampBoard == null || stampBoard.isNotOwner(findMember.getId())) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}
		//TODO jjh 삭제 개선
		stampBoardRepository.updateIsDeletedById(stampBoardId, true);
		eventPublisher.publishEvent(new StampBoardDeletedEvent(findMember));
	}

	@Transactional
	public void issueCoupon(final MemberDto guardian, final long stampBoardId, long rewardDate) {
		StampBoard stampBoard = stampBoardRepository.findByIdAndIsDeleted(stampBoardId, false);
		validateIssueCoupon(guardian, stampBoard);

		stampBoard.issueCoupon(rewardDate);
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
	public void createMissionRequest(final String username,
		final MissionRequestCreateRequest missionRequestCreateRequest) {
		Member kid = userService.findMemberByUsername(username);
		StampBoard stampBoard = getStampBoard(missionRequestCreateRequest.stampBoardId());

		validateForCreateMission(kid, stampBoard);

		Mission mission = getMission(missionRequestCreateRequest.missionId());
		Member guardianEntity = userService.findMemberByMemberId(missionRequestCreateRequest.guardianId());
		Member kidEntity = userService.findMemberByMemberId(kid.getId());

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
	public void deleteMissionRequest(long memberId, long missionRequestId) {
		MissionRequest missionRequest = missionRequestRepository.getReferenceById(missionRequestId);
		if (missionRequest.isNotOwner(memberId)) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}
		missionRequestRepository.delete(missionRequest);
	}

	@Transactional
	public void deleteMissionRequests(long stampBoardId) {
		missionRequestRepository.deleteByStampBoardId(stampBoardId);
	}

	//Stamp
	@Transactional
	public void createStamp(final String username, long stampBoardId, StampCreateRequest stampCreateRequest) {
		Member guardian = userService.findMemberByUsername(username);
		StampBoard stampBoard = getStampBoard(stampBoardId);
		validateForCreateStamp(stampBoard, guardian.getId());

		Mission mission = getMissionByRequest(stampCreateRequest);
		if (mission.isNotOwner(guardian.getId())) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}
		stampRepository.save(createStamp(stampBoard, mission, stampCreateRequest.stampDesignId()));
		stampBoard.addStampCount();
		if (stampCreateRequest.missionRequestId() != null) {
			deleteMissionRequest(guardian.getId(), stampCreateRequest.missionRequestId());
		}

		Member kid = userService.findMemberByMemberId(stampBoard.getKidId());
		eventPublisher.publishEvent(new StampCreatedEvent(List.of(guardian, kid)));
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

	private List<StampBoardSummary> getStampBoardSummaries(final Member member, final long partnerId,
		final StampBoardGroup stampBoardGroup) {
		List<StampBoard> stampBoards;
		if (member.isKid()) {
			stampBoards = getStampBoards(partnerId, member.getId());
		} else {
			stampBoards = getStampBoards(member.getId(), partnerId);
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

	private void validateIssueCoupon(MemberDto guardian, StampBoard stampBoard) {
		if (stampBoard == null || stampBoard.isNotOwner(guardian.memberId())) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}
		if (!stampBoard.isCompleted()) {
			throw new IllegalArgumentException("stamp board is not completed.");
		}
	}

	//Mission
	private void validateForCreateMission(final Member member, StampBoard stampBoard) {
		if (!member.isKid() || stampBoard.isNotOwner(member.getId())) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}
		if (stampBoard.isCompleted()) {
			throw new IllegalArgumentException("이미 도장을 다 모았습니다.");
		}
	}

	private Mission getMissionByRequest(StampCreateRequest stampCreateRequest) {
		if (stampCreateRequest.missionRequestId() != null) {
			MissionRequest missionRequest = missionRequestRepository.getReferenceById(
				stampCreateRequest.missionRequestId());
			return missionRequest.getMission();
		}
		if (stampCreateRequest.missionId() != null) {
			return missionRepository.getReferenceById(stampCreateRequest.missionId());
		}
		throw new IllegalArgumentException("mission info is null");
	}

	//Stamp
	private Stamp createStamp(final StampBoard stampBoard, final Mission mission, final int stampDesignId) {
		return Stamp.createStamp()
			.stampBoard(stampBoard)
			.mission(mission)
			.stampDesignId(stampDesignId)
			.build();
	}

	private void validateForCreateStamp(StampBoard stampBoard, final long memberId) {
		if (stampBoard.isNotOwner(memberId)) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}
		if (stampBoard.isCompleted()) {
			throw new IllegalArgumentException("이미 도장을 다 모았습니다.");
		}
	}

	private StampBoard createStampBoard(final StampBoardCreateRequest stampBoardCreateRequest,
		final Member findMember) {
		return StampBoard.createStampBoard()
			.guardianId(findMember.getId())
			.kidId(stampBoardCreateRequest.kidId())
			.name(stampBoardCreateRequest.name())
			.goalStampCount(stampBoardCreateRequest.goalStampCount())
			.reward(stampBoardCreateRequest.reward())
			.build();
	}
}
