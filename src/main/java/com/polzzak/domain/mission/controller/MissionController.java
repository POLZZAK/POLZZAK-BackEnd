package com.polzzak.domain.mission.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.polzzak.domain.mission.dto.MissionCompleteCreateRequest;
import com.polzzak.domain.mission.entity.Mission;
import com.polzzak.domain.mission.service.MissionService;
import com.polzzak.domain.stamp.entity.StampBoard;
import com.polzzak.domain.stamp.service.StampBoardService;
import com.polzzak.domain.user.dto.MemberDto;
import com.polzzak.domain.user.service.UserService;
import com.polzzak.global.common.ApiResponse;
import com.polzzak.global.exception.ErrorCode;
import com.polzzak.global.exception.PolzzakException;
import com.polzzak.global.security.LoginUsername;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/mission")
public class MissionController {

	private final MissionService missionService;
	private final UserService userService;
	private final StampBoardService stampBoardService;

	@PostMapping("/complete")
	public ResponseEntity<?> createMissionComplete(
		@LoginUsername String username, @RequestBody @Valid MissionCompleteCreateRequest missionCompleteCreateRequest
	) {
		MemberDto member = userService.getMemberInfo(username);
		StampBoard stampBoard = stampBoardService.getStampBoard(missionCompleteCreateRequest.stampBoardId());
		if (!member.isKid() || stampBoard.isNotOwner(member.memberId())) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}
		Mission mission = missionService.getMission(missionCompleteCreateRequest.missionId());

		missionService.createMissionComplete(stampBoard, mission, missionCompleteCreateRequest.guardianId(), member);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created());
	}
}
