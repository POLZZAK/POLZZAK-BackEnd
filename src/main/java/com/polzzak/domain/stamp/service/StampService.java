package com.polzzak.domain.stamp.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polzzak.domain.mission.entity.Mission;
import com.polzzak.domain.mission.service.MissionService;
import com.polzzak.domain.stamp.dto.StampCreateRequest;
import com.polzzak.domain.stamp.dto.StampDto;
import com.polzzak.domain.stamp.entity.Stamp;
import com.polzzak.domain.stamp.entity.StampBoard;
import com.polzzak.domain.stamp.repository.StampRepository;
import com.polzzak.domain.user.dto.MemberDto;
import com.polzzak.global.exception.ErrorCode;
import com.polzzak.global.exception.PolzzakException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class StampService {

	private final StampBoardService stampBoardService;
	private final MissionService missionService;
	private final StampRepository stampRepository;

	@Transactional
	public void createStamp(MemberDto member, long stampBoardId, StampCreateRequest stampCreateRequest) {
		StampBoard stampBoard = stampBoardService.getStampBoard(stampBoardId);
		validateForCreateStamp(stampBoard, member);
		int stampCount = getValidStampCountForAdd(stampBoard, stampCreateRequest.count());

		List<Stamp> stamps = new ArrayList<>(stampCount);

		Mission mission = missionService.getMission(stampCreateRequest.missionId());
		for (int i = 0; i < stampCount; i++) {
			stamps.add(Stamp.createMission()
				.stampBoard(stampBoard)
				.mission(mission)
				.stampDesignId(stampCreateRequest.stampDesignId())
				.build());
		}
		stampRepository.saveAll(stamps);

		stampBoard.setCurrentStampCount(stampBoard.getCurrentStampCount() + stampCount);
		if (stampBoard.isCompleted()) {
			stampBoard.complete();
		}
	}

	public StampDto getStampDto(long stampBoardId, long stampId) {
		StampBoard stampBoard = stampBoardService.getStampBoard(stampBoardId);
		if (!stampBoard.isValidStamp(stampId)) {
			throw new IllegalArgumentException("유효하지 않은 도장입니다.");
		}
		return StampDto.from(stampRepository.getReferenceById(stampId));
	}

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
