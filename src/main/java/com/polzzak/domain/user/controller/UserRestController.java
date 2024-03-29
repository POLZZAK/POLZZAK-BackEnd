package com.polzzak.domain.user.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.polzzak.domain.user.dto.MemberResponse;
import com.polzzak.domain.user.dto.UpdateNicknameRequest;
import com.polzzak.domain.user.service.UserService;
import com.polzzak.global.common.ApiResponse;
import com.polzzak.global.security.LoginId;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

	private final UserService userService;

	public UserRestController(final UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<MemberResponse>> getMemberInfo(final @LoginId Long memberId) {
		return ResponseEntity.ok(ApiResponse.ok(userService.getMemberResponse(memberId)));
	}

	@PatchMapping(path = "/profile")
	public ResponseEntity<Void> updateUserProfile(
		final @LoginId Long memberId,
		final @RequestPart(value = "profile", required = false) MultipartFile profile
	) {
		String profileKey = null;
		if (profile != null) {
			profileKey = userService.uploadProfile(profile);
		}
		Optional<String> prevProfileKey = userService.updateMemberProfile(memberId, profileKey);
		prevProfileKey.ifPresent(userService::deleteProfile);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping(path = "/nickname")
	public ResponseEntity<Void> updateUserNickname(
		final @LoginId Long memberId,
		final @RequestBody UpdateNicknameRequest request
	) {
		userService.updateNickname(memberId, request);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping
	public ResponseEntity<Void> withdrawUser(final @LoginId Long memberId) {
		userService.withdrawMember(memberId);
		return ResponseEntity.noContent().build();
	}
}
