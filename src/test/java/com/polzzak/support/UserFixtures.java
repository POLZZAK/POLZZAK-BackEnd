package com.polzzak.support;

import static com.polzzak.support.FamilyFixtures.*;
import static com.polzzak.support.MemberPointFixtures.*;
import static com.polzzak.support.MemberTypeFixtures.*;

import org.springframework.mock.web.MockMultipartFile;

import com.polzzak.domain.membertype.entity.MemberType;
import com.polzzak.domain.user.dto.LoginRequest;
import com.polzzak.domain.user.dto.MemberDto;
import com.polzzak.domain.user.dto.MemberResponse;
import com.polzzak.domain.user.dto.MemberTypeDto;
import com.polzzak.domain.user.dto.RegisterRequest;
import com.polzzak.domain.user.dto.UserDto;
import com.polzzak.domain.user.entity.SocialType;
import com.polzzak.domain.user.entity.UserRole;

public class UserFixtures {
	public static final Long TEST_MEMBER_ID = 0L;
	public static final Long TEST_TARGET_MEMBER_ID = 1L;
	public static final String TEST_USERNAME = "username";
	public static final String TEST_NICKNAME = "nickname";
	public static final String TEST_PROFILE_KEY = "testProfileKey";
	public static final String TEST_PREV_PROFILE_KEY = "testPrevProfileKey";
	public static final String TEST_PROFILE_URL = "profileUrl";
	public static final String TEST_OAUTH_ACCESS_TOKEN = "oAuthAccessToken";
	public static final String TEST_USER_ROLE = UserRole.ROLE_USER.name();
	public static final String TEST_ADMIN_ROLE = UserRole.ROLE_ADMIN.name();
	public static final MemberTypeDto MEMBER_GUARDIAN_TYPE_DTO = new MemberTypeDto(MemberType.GUARDIAN.name(), "보호자");
	public static final RegisterRequest REGISTER_KID_REQUEST = new RegisterRequest(TEST_USERNAME, SocialType.KAKAO,
		TEST_DETAIL_ID, TEST_NICKNAME);
	public static final RegisterRequest REGISTER_MOT_EXIST_MEMBER_TYPE_REQUEST = new RegisterRequest(TEST_USERNAME,
		SocialType.KAKAO, TEST_NOT_EXIST_DETAIL_ID, TEST_NICKNAME);
	public static final MockMultipartFile TEST_PROFILE =
		new MockMultipartFile("profile", "originalFilename", "image/png", "content".getBytes());
	public static final LoginRequest LOGIN_REQUEST = new LoginRequest(TEST_OAUTH_ACCESS_TOKEN);
	public static final MemberDto MEMBER_GUARDIAN_DTO = new MemberDto(TEST_MEMBER_ID, TEST_NICKNAME,
		MEMBER_GUARDIAN_TYPE_DTO);
	public static final MemberResponse MEMBER_RESPONSE = new MemberResponse(TEST_MEMBER_ID, TEST_NICKNAME,
		TEST_MEMBER_POINT_DTO, MEMBER_GUARDIAN_TYPE_DTO, TEST_PROFILE_URL, FAMILY_DEFAULT_COUNT);
	public static final String TEST_SOCIAL_TYPE = "kakao";
	public static final UserDto USER_DTO = new UserDto(TEST_MEMBER_ID, TEST_USER_ROLE);
}
