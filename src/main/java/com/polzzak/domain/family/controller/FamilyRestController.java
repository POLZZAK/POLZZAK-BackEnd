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
import com.polzzak.domain.family.dto.FamilyNewRequestMarkDto;
import com.polzzak.domain.family.dto.SearchedMemberDto;
import com.polzzak.domain.family.service.FamilyMapService;
import com.polzzak.global.common.ApiResponse;
import com.polzzak.global.security.LoginId;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/families")
public class FamilyRestController {

	private final FamilyMapService familyMapService;

	public FamilyRestController(final FamilyMapService familyMapService) {
		this.familyMapService = familyMapService;
	}

	@GetMapping("/users")
	public ResponseEntity<ApiResponse<SearchedMemberDto>> getMemberByNickname(
		final @LoginId Long memberId,
		final @RequestParam("nickname") String nickname
	) {
		return ResponseEntity.ok(ApiResponse.ok(familyMapService.getSearchedMemberByNickname(memberId, nickname)));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Void>> createFamilyMap(
		final @LoginId Long memberId,
		final @RequestBody @Valid FamilyMapRequest familyMapRequest
	) {
		familyMapService.saveFamilyTempMap(memberId, familyMapRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created());
	}

	@PatchMapping("/approve/{id}")
	public ResponseEntity<Void> approveFamilyMap(
		final @LoginId Long memberId,
		final @PathVariable("id") Long id
	) {
		familyMapService.approveFamilyMap(memberId, id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteFamilyMap(
		final @LoginId Long memberId,
		final @PathVariable("id") Long id
	) {
		familyMapService.deleteFamilyMap(memberId, id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/reject/{id}")
	public ResponseEntity<Void> rejectFamilyMap(
		final @LoginId Long memberId,
		final @PathVariable("id") Long id
	) {
		familyMapService.rejectFamilyRequest(memberId, id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/cancel/{id}")
	public ResponseEntity<Void> cancelFamilyMap(
		final @LoginId Long memberId,
		final @PathVariable("id") Long id
	) {
		familyMapService.cancelFamilyRequest(memberId, id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public ResponseEntity<ApiResponse<FamilyMemberListResponse>> getUserFamilies(final @LoginId Long memberId) {
		return ResponseEntity.ok(
			ApiResponse.ok(FamilyMemberListResponse.from(familyMapService.getMyFamilies(memberId))));
	}

	@GetMapping("/requests/sent")
	public ResponseEntity<ApiResponse<FamilyMemberListResponse>> getMySentList(final @LoginId Long memberId) {
		return ResponseEntity.ok(
			ApiResponse.ok(FamilyMemberListResponse.from(familyMapService.getMySentList(memberId))));
	}

	@GetMapping("/requests/received")
	public ResponseEntity<ApiResponse<FamilyMemberListResponse>> getMyReceivedList(final @LoginId Long memberId) {
		return ResponseEntity.ok(
			ApiResponse.ok(FamilyMemberListResponse.from(familyMapService.getMyReceivedList(memberId))));
	}

	@GetMapping("/new-request-mark")
	public ResponseEntity<ApiResponse<FamilyNewRequestMarkDto>> getNewRequestMark(
		final @LoginId Long memberId) {
		return ResponseEntity.ok(ApiResponse.ok(familyMapService.getFamilyNewRequestMark(memberId)));
	}
}
