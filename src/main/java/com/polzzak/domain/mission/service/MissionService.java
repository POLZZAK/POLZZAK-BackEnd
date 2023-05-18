package com.polzzak.domain.mission.service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polzzak.domain.mission.dto.MissionDto;
import com.polzzak.domain.mission.entity.Mission;
import com.polzzak.domain.mission.entity.MissionComplete;
import com.polzzak.domain.mission.repository.MissionCompleteRepository;
import com.polzzak.domain.mission.repository.MissionRepository;
import com.polzzak.domain.stamp.entity.StampBoard;
import com.polzzak.domain.user.dto.MemberDto;
import com.polzzak.domain.user.entity.Member;
import com.polzzak.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MissionService {

	private final UserService userService;
	private final MissionRepository missionRepository;
	private final MissionCompleteRepository missionCompleteRepository;

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
	public void createMissionComplete(StampBoard stampBoard, Mission mission, long guardianId, MemberDto kid) {
		if (stampBoard.isCompleted()) {
			throw new IllegalArgumentException("이미 도장을 다 모았습니다.");
		}
		Member guardianEntity = userService.findMemberByMemberId(guardianId);
		Member kidEntity = userService.findMemberByMemberId(kid.memberId());

		MissionComplete missionComplete = MissionComplete.createMissionComplete()
			.stampBoard(stampBoard)
			.mission(mission)
			.guardian(guardianEntity)
			.kid(kidEntity)
			.build();

		missionCompleteRepository.save(missionComplete);
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
			.forEach(mission -> mission.setActive(false));

		List<Mission> newMissions = afterMissionDtoList.stream()
			.filter(missionDto -> missionDto.id() == null)
			.map(missionDto -> Mission.createMission()
				.stampBoard(stampBoard)
				.content(missionDto.content())
				.build())
			.toList();

		missionRepository.saveAll(newMissions);
	}
}
