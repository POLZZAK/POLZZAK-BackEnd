package com.polzzak.domain.family.controller;

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

import com.polzzak.domain.family.dto.FamilyMapRequest;
import com.polzzak.domain.family.dto.FamilyMemberListResponse;
import com.polzzak.domain.family.service.FamilyMapService;
import com.polzzak.global.common.ApiResponse;
import com.polzzak.global.security.LoginUsername;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/families")
public class FamilyRestController {

	private final FamilyMapService familyMapService;

	public FamilyRestController(final FamilyMapService familyMapService) {
		this.familyMapService = familyMapService;
	}

	@GetMapping("/users")
	public ResponseEntity<ApiResponse> getMemberByNickname(
		final @LoginUsername String username,
		final @RequestParam("nickname") String nickname
	) {
		return ResponseEntity.ok(ApiResponse.ok(familyMapService.getSearchedMemberByNickname(username, nickname)));
	}

	@PostMapping
	public ResponseEntity<ApiResponse> createFamilyMap(
		final @LoginUsername String username,
		final @RequestBody @Valid FamilyMapRequest familyMapRequest
	) {
		familyMapService.saveFamilyTempMap(username, familyMapRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created());
	}

	@PatchMapping("/approve/{id}")
	public ResponseEntity approveFamilyMap(
		final @LoginUsername String username,
		final @PathVariable("id") Long id
	) {
		familyMapService.approveFamilyMap(username, id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/reject/{id}")
	public ResponseEntity<ApiResponse> rejectFamilyMap(
		final @LoginUsername String username,
		final @PathVariable("id") Long id
	) {
		familyMapService.rejectFamilyMap(username, id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/cancel/{id}")
	public ResponseEntity<ApiResponse> cancelFamilyMap(
		final @LoginUsername String username,
		final @PathVariable("id") Long id
	) {
		familyMapService.cancelFamilyMap(username, id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public ResponseEntity<ApiResponse> getUserFamilies(final @LoginUsername String username) {
		return ResponseEntity.ok(
			ApiResponse.ok(FamilyMemberListResponse.from(familyMapService.getMyFamilies(username))));
	}

	@GetMapping("/requests/sent")
	public ResponseEntity<ApiResponse> getMySentList(final @LoginUsername String username) {
		return ResponseEntity.ok(
			ApiResponse.ok(FamilyMemberListResponse.from(familyMapService.getMySentList(username))));
	}

	@GetMapping("/requests/received")
	public ResponseEntity<ApiResponse> getMyReceivedList(final @LoginUsername String username) {
		return ResponseEntity.ok(
			ApiResponse.ok(FamilyMemberListResponse.from(familyMapService.getMyReceivedList(username))));
	}
}
