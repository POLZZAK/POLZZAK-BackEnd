package com.polzzak.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.polzzak.domain.family.repository.FamilyMapRepository;
import com.polzzak.domain.memberpoint.entity.MemberPoint;
import com.polzzak.domain.memberpoint.repository.MemberPointRepository;
import com.polzzak.domain.user.dto.MemberDto;
import com.polzzak.domain.user.dto.MemberResponse;
import com.polzzak.domain.user.entity.Member;
import com.polzzak.domain.user.repository.MemberRepository;
import com.polzzak.domain.user.repository.UserRepository;
import com.polzzak.global.common.FileType;
import com.polzzak.global.exception.ErrorCode;
import com.polzzak.global.exception.PolzzakException;
import com.polzzak.global.infra.file.FileClient;

@Service
@Transactional(readOnly = true)
public class UserService {
	private final UserRepository userRepository;
	private final MemberRepository memberRepository;

	private final FileClient fileClient;
	private final MemberPointRepository memberPointRepository;
	private final FamilyMapRepository familyMapRepository;

	public UserService(final UserRepository userRepository, final MemberRepository memberRepository,
		final FileClient fileClient, final MemberPointRepository memberPointRepository,
		final FamilyMapRepository familyMapRepository) {
		this.userRepository = userRepository;
		this.memberRepository = memberRepository;
		this.fileClient = fileClient;
		this.memberPointRepository = memberPointRepository;
		this.familyMapRepository = familyMapRepository;
	}

	public MemberResponse getMemberResponse(final long memberId) {
		Member requestMember = findMemberByMemberIdWithMemberType(memberId);
		MemberPoint memberPoint = memberPointRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException(String.format("사용자 %d의 포인트가 존재하지 않습니다", memberId)));
		int familyCount = getFamilyCount(requestMember);
		return MemberResponse.from(requestMember, memberPoint, fileClient.getSignedUrl(requestMember.getProfileKey()),
			familyCount);
	}

	public MemberDto getMemberInfo(final String username) {
		Member findMember = findMemberByUsername(username);
		return MemberDto.from(findMember);
	}

	public MemberDto getGuardianInfo(final String username) {
		Member findMember = findMemberByUsername(username);
		if (findMember.isKid()) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}
		return MemberDto.from(findMember);
	}

	public MemberDto getKidInfo(final String username) {
		Member findMember = findMemberByUsername(username);
		if (!findMember.isKid()) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}
		return MemberDto.from(findMember);
	}

	public Member findMemberByUsername(final String username) {
		return userRepository.findByUsername(username)
			.orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다"))
			.getMember();
	}

	public Member findMemberByMemberId(final Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다"));
	}

	public Member findMemberByMemberIdWithMemberType(final Long memberId) {
		return memberRepository.findByIdWithMemberTypeDetail(memberId)
			.orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다"));
	}

	@Transactional
	public String updateMemberProfile(final Long memberId, final String profileKey) {
		final Member member = findMemberByMemberId(memberId);
		final String prevProfileKey = member.getProfileKey();
		member.changeProfileKey(profileKey);
		return prevProfileKey;
	}

	public String uploadProfile(final MultipartFile profile) {
		return fileClient.uploadFile(profile, FileType.PROFILE_IMAGE);
	}

	public void deleteProfile(final String prevProfileKey) {
		fileClient.deleteFile(prevProfileKey);
	}

	private int getFamilyCount(final Member requestMember) {
		if (requestMember.isKid()) {
			return familyMapRepository.countByKidId(requestMember.getId());
		}
		return familyMapRepository.countByGuardianId(requestMember.getId());
	}
}
