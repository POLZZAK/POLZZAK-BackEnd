package com.polzzak.domain.user.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polzzak.domain.user.dto.MemberDto;
import com.polzzak.domain.user.entity.Member;
import com.polzzak.domain.user.repository.MemberRepository;
import com.polzzak.domain.user.repository.UserRepository;
import com.polzzak.global.exception.ErrorCode;
import com.polzzak.global.exception.PolzzakException;
import com.polzzak.global.infra.file.FileClient;

@Service
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;

	private final MemberRepository memberRepository;

	private final FileClient fileClient;

	public UserService(final UserRepository userRepository, final MemberRepository memberRepository,
		final FileClient fileClient) {
		this.userRepository = userRepository;
		this.memberRepository = memberRepository;
		this.fileClient = fileClient;
	}

	public MemberDto getMemberInfo(final String username) {
		Member findMember = findMemberByUsername(username);
		return MemberDto.from(findMember, fileClient.getSignedUrl(findMember.getProfileKey()));
	}

	public MemberDto getGuardianInfo(final String username) {
		Member findMember = findMemberByUsername(username);
		if (findMember.isKid()) {
			throw new PolzzakException(ErrorCode.FORBIDDEN);
		}
		return MemberDto.from(findMember, fileClient.getSignedUrl(findMember.getProfileKey()));
	}

	public Optional<Member> getMemberByNickname(final String username, final String nickname) {
		Member findMember = findMemberByUsername(username);

		return memberRepository.searchByNickname(nickname)
			.filter(searchedMember -> !findMember.getNickname().equals(searchedMember.getNickname()));
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
}
