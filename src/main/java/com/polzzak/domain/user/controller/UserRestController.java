package com.polzzak.domain.user.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

	@PatchMapping(path = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Void> updateUserProfile(
		final @LoginId Long memberId,
		final @RequestPart("profile") MultipartFile profile
	) {
		final String profileKey = userService.uploadProfile(profile);
		final String prevProfileKey = userService.updateMemberProfile(memberId, profileKey);
		userService.deleteProfile(prevProfileKey);
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
}
