package com.polzzak.domain.stampboard.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.polzzak.domain.stampboard.dto.MissionCompleteCreateRequest;
import com.polzzak.domain.stampboard.service.StampBoardService;
import com.polzzak.global.common.ApiResponse;
import com.polzzak.global.security.LoginUsername;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/missions")
public class MissionController {

	private final StampBoardService stampBoardService;

	@PostMapping("/complete")
	public ResponseEntity<ApiResponse<Void>> createMissionComplete(
		@LoginUsername String username, @RequestBody @Valid MissionCompleteCreateRequest missionCompleteCreateRequest
	) {
		stampBoardService.createMissionComplete(missionCompleteCreateRequest, username);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created());
	}
}
