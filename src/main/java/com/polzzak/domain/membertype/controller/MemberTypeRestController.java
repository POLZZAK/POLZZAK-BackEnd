package com.polzzak.domain.membertype.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.polzzak.domain.membertype.dto.MemberTypeDetailListDto;
import com.polzzak.domain.membertype.dto.MemberTypeDetailRequest;
import com.polzzak.domain.membertype.service.MemberTypeDetailService;
import com.polzzak.global.common.ApiResponse;
import com.polzzak.global.security.LoginUsername;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/member-types")
public class MemberTypeRestController {

	private final MemberTypeDetailService memberTypeDetailService;

	public MemberTypeRestController(final MemberTypeDetailService memberTypeDetailService) {
		this.memberTypeDetailService = memberTypeDetailService;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<MemberTypeDetailListDto>> getAllMemberTypeDetailList() {
		return ResponseEntity.ok(ApiResponse.ok(memberTypeDetailService.findMemberTypeDetailList()));
	}

	@PostMapping
	public ResponseEntity<Void> createMemberType(
		final @LoginUsername(administrator = true) String username,
		final @RequestBody @Valid MemberTypeDetailRequest request
	) {
		memberTypeDetailService.saveMemberTypeDetail(request);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteMemberTypeDetail(
		final @LoginUsername(administrator = true) String username,
		final @PathVariable("id") Long id
	) {
		memberTypeDetailService.deleteMemberTypeDetailById(id);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<Void> updateMemberTypeDetail(
		final @LoginUsername(administrator = true) String username,
		final @PathVariable("id") Long id,
		final @RequestBody @Valid MemberTypeDetailRequest request
	) {
		memberTypeDetailService.updateMemberTypeDetail(id, request);
		return ResponseEntity.noContent().build();
	}
}
