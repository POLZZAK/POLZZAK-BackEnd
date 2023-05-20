package com.polzzak.domain.stamp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.polzzak.domain.stamp.dto.FamilyStampBoardSummary;
import com.polzzak.domain.stamp.dto.StampBoardCreateRequest;
import com.polzzak.domain.stamp.dto.StampBoardDto;
import com.polzzak.domain.stamp.dto.StampBoardUpdateRequest;
import com.polzzak.domain.stamp.dto.StampCreateRequest;
import com.polzzak.domain.stamp.dto.StampDto;
import com.polzzak.domain.stamp.entity.StampBoard;
import com.polzzak.domain.stamp.service.StampBoardService;
import com.polzzak.domain.stamp.service.StampService;
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
@RequestMapping("/api/v1/stamp")
public class StampController {

	private final StampBoardService stampBoardService;
	private final UserService userService;
	private final StampService stampService;

	//StampBoard
	@PostMapping("/stamp-board")
	public ResponseEntity<?> createStampBoard(
		@LoginUsername String username, @RequestBody @Valid StampBoardCreateRequest stampBoardCreateRequest
	) {
		MemberDto member = userService.getMemberInfo(username);
		if (member.isKid()) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}

		stampBoardService.createStampBoard(member, stampBoardCreateRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created());
	}

	@GetMapping("/stamp-boards")
	public ResponseEntity<ApiResponse<List<FamilyStampBoardSummary>>> getStampBoards(
		@LoginUsername String username, @RequestParam(required = false) Long partnerMemberId,
		@RequestParam boolean isInProgress
	) {
		MemberDto member = userService.getMemberInfo(username);
		return ResponseEntity.ok(
			ApiResponse.ok(stampBoardService.getFamilyStampBoardSummaries(member, partnerMemberId, isInProgress)));
	}

	@GetMapping("/stamp-board/{stampBoardId}")
	public ResponseEntity<ApiResponse<StampBoardDto>> getStampBoard(@LoginUsername String username,
		@PathVariable long stampBoardId) {
		MemberDto member = userService.getMemberInfo(username);
		return ResponseEntity.ok(ApiResponse.ok(stampBoardService.getStampBoardDto(member, stampBoardId)));
	}

	@DeleteMapping("/stamp-board/{stampBoardId}")
	public ResponseEntity<Void> deleteStampBoard(@LoginUsername String username,
		@PathVariable long stampBoardId) {
		MemberDto member = userService.getMemberInfo(username);
		if (member.isKid()) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}

		stampBoardService.deleteStampBoard(member, stampBoardId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@PutMapping("/stamp-board/{stampBoardId}")
	public ResponseEntity<ApiResponse<StampBoardDto>> updateStampBoard(@LoginUsername String username,
		@PathVariable long stampBoardId,
		@RequestBody @Valid StampBoardUpdateRequest stampBoardUpdateRequest) {
		MemberDto member = userService.getMemberInfo(username);
		if (member.isKid()) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}

		StampBoardDto stampBoard = stampBoardService.updateStampBoard(member, stampBoardId, stampBoardUpdateRequest);
		return ResponseEntity.ok(ApiResponse.ok(stampBoard));
	}

	//Stamp
	@PostMapping("/stamp-board/{stampBoardId}/stamp")
	public ResponseEntity<?> createStamp(
		@LoginUsername String username, @PathVariable long stampBoardId,
		@RequestBody @Valid StampCreateRequest stampCreateRequest
	) {
		MemberDto member = userService.getMemberInfo(username);
		if (member.isKid()) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}

		stampService.createStamp(member, stampBoardId, stampCreateRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created());
	}

	@GetMapping("/stamp-board/{stampBoardId}/{stampId}")
	public ResponseEntity<ApiResponse<StampDto>> getStamp(
		@PathVariable long stampBoardId,
		@PathVariable long stampId
	) {
		return ResponseEntity.ok(ApiResponse.ok(stampService.getStampDto(stampBoardId, stampId)));
	}
}
