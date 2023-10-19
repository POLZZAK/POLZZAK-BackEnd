package com.polzzak.domain.pushtoken.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.polzzak.domain.pushtoken.model.PushToken;
import com.polzzak.domain.pushtoken.repository.PushTokenRepository;
import com.polzzak.domain.user.entity.Member;
import com.polzzak.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PushTokenService {

	private final UserService userService;
	private final PushTokenRepository pushTokenRepository;

	public void addToken(final Long memberId, final String token) {
		Member member = userService.findMemberByMemberId(memberId);

		PushToken pushToken = PushToken.createPushToken()
			.member(member)
			.token(token)
			.build();

		try {
			pushTokenRepository.save(pushToken);
		} catch (DataIntegrityViolationException e) {

		}
	}

	public List<PushToken> getPushTokens(final Member member) {
		return pushTokenRepository.getPushTokensByMember(member);
	}
}
