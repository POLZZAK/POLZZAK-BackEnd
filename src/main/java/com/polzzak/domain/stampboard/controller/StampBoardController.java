package com.polzzak.domain.stampboard.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.polzzak.domain.stampboard.dto.FamilyStampBoardSummary;
import com.polzzak.domain.stampboard.dto.MissionRequestCreateRequest;
import com.polzzak.domain.stampboard.dto.StampBoardCreateRequest;
import com.polzzak.domain.stampboard.dto.StampBoardDto;
import com.polzzak.domain.stampboard.dto.StampBoardGroup;
import com.polzzak.domain.stampboard.dto.StampBoardUpdateRequest;
import com.polzzak.domain.stampboard.dto.StampCreateRequest;
import com.polzzak.domain.stampboard.dto.StampDto;
import com.polzzak.domain.stampboard.service.StampBoardService;
import com.polzzak.domain.user.dto.MemberDto;
import com.polzzak.domain.user.service.UserService;
import com.polzzak.global.common.ApiResponse;
import com.polzzak.global.security.LoginUsername;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/stamps")
public class StampBoardController {

	private final StampBoardService stampBoardService;
	private final UserService userService;

	//StampBoard
	@PostMapping("/stamp-boards")
	public ResponseEntity<ApiResponse<Void>> createStampBoard(
		@LoginUsername String username, @RequestBody @Valid StampBoardCreateRequest stampBoardCreateRequest
	) {
		stampBoardService.createStampBoard(username, stampBoardCreateRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created());
	}

	@GetMapping("/stamp-boards")
	public ResponseEntity<ApiResponse<List<FamilyStampBoardSummary>>> getStampBoards(
		@LoginUsername String username, @RequestParam(required = false) Long partnerMemberId,
		@RequestParam(value = "stampBoardGroup") String stampBoardGroupAsStr
	) {
		StampBoardGroup stampBoardGroup = StampBoardGroup.getStampBoardGroupByStr(stampBoardGroupAsStr);
		return ResponseEntity.ok(
			ApiResponse.ok(stampBoardService.getFamilyStampBoardSummaries(username, partnerMemberId, stampBoardGroup)));
	}

	@GetMapping("/stamp-boards/{stampBoardId}")
	public ResponseEntity<ApiResponse<StampBoardDto>> getStampBoard(@LoginUsername String username,
		@PathVariable long stampBoardId) {
		MemberDto member = userService.getMemberInfo(username);
		return ResponseEntity.ok(ApiResponse.ok(stampBoardService.getStampBoardDto(member, stampBoardId)));
	}

	@DeleteMapping("/stamp-boards/{stampBoardId}")
	public ResponseEntity<Void> deleteStampBoard(@LoginUsername String username, @PathVariable long stampBoardId) {
		stampBoardService.deleteStampBoard(username, stampBoardId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@PatchMapping("/stamp-boards/{stampBoardId}")
	public ResponseEntity<ApiResponse<StampBoardDto>> updateStampBoard(@LoginUsername String username,
		@PathVariable long stampBoardId,
		@RequestBody @Valid StampBoardUpdateRequest stampBoardUpdateRequest) {
		MemberDto guardian = userService.getGuardianInfo(username);
		StampBoardDto stampBoard = stampBoardService.updateStampBoard(guardian, stampBoardId, stampBoardUpdateRequest);
		return ResponseEntity.ok(ApiResponse.ok(stampBoard));
	}

	//Stamp
	@PostMapping("/stamp-boards/{stampBoardId}/stamp")
	public ResponseEntity<?> createStamp(
		@LoginUsername String username, @PathVariable long stampBoardId,
		@RequestBody @Valid StampCreateRequest stampCreateRequest
	) {
		stampBoardService.createStamp(username, stampBoardId, stampCreateRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created());
	}

	@GetMapping("/stamp-boards/{stampBoardId}/{stampId}")
	public ResponseEntity<ApiResponse<StampDto>> getStamp(
		@PathVariable long stampBoardId,
		@PathVariable long stampId
	) {
		return ResponseEntity.ok(ApiResponse.ok(stampBoardService.getStampDto(stampBoardId, stampId)));
	}

	//Mission
	@PostMapping("/mission-requests")
	public ResponseEntity<ApiResponse<Void>> createmissionRequest(
		@LoginUsername String username, @RequestBody @Valid MissionRequestCreateRequest missionRequestCreateRequest
	) {
		stampBoardService.createMissionRequest(username, missionRequestCreateRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created());
	}

	@DeleteMapping("/mission-requests/{missionRequestId}")
	public ResponseEntity<ApiResponse<Void>> deleteMissionRequest(
		@LoginUsername String username, @PathVariable long missionRequestId
	) {
		MemberDto guardian = userService.getGuardianInfo(username);
		stampBoardService.deleteMissionRequest(guardian.memberId(), missionRequestId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
