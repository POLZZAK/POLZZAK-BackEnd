package com.polzzak.domain.ranking.service;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polzzak.domain.memberpoint.entity.MemberPoint;
import com.polzzak.domain.memberpoint.repository.MemberPointRepository;
import com.polzzak.domain.ranking.dto.GuardianRankingSummaryDto;
import com.polzzak.domain.ranking.dto.KidRankingSummaryDto;
import com.polzzak.domain.ranking.dto.RakingSummaryListResponse;
import com.polzzak.domain.ranking.repository.GuardianRankingSummaryRepository;
import com.polzzak.domain.ranking.repository.KidRankingSummaryRepository;
import com.polzzak.domain.user.dto.MemberSimpleResponse;
import com.polzzak.domain.user.entity.Member;
import com.polzzak.domain.user.repository.MemberRepository;
import com.polzzak.global.infra.file.FileClient;

@Service
public class RankingService {
	private final GuardianRankingSummaryRepository guardianRankingSummaryRepository;
	private final KidRankingSummaryRepository kidRankingSummaryRepository;
	private final MemberRepository memberRepository;
	private final MemberPointRepository memberPointRepository;
	private final FileClient fileClient;

	public RankingService(final GuardianRankingSummaryRepository guardianRankingSummaryRepository,
		final KidRankingSummaryRepository kidRankingSummaryRepository, MemberRepository memberRepository,
		MemberPointRepository memberPointRepository, final FileClient fileClient) {
		this.guardianRankingSummaryRepository = guardianRankingSummaryRepository;
		this.kidRankingSummaryRepository = kidRankingSummaryRepository;
		this.memberRepository = memberRepository;
		this.memberPointRepository = memberPointRepository;
		this.fileClient = fileClient;
	}

	@Transactional(readOnly = true)
	public RakingSummaryListResponse getGuardianRankingSummaries(final Long memberId) {
		MemberSimpleResponse memberSimpleResponse = getMemberSimpleResponse(memberId);
		return new RakingSummaryListResponse(memberSimpleResponse, guardianRankingSummaryRepository.findAll()
			.stream()
			.map(guardianRankingSummary -> {
				if (guardianRankingSummary.getNickname().equals(memberSimpleResponse.nickname())) {
					return GuardianRankingSummaryDto.of(guardianRankingSummary,
						fileClient.getSignedUrl(guardianRankingSummary.getProfileKey()), true);
				}
				return GuardianRankingSummaryDto.of(
					guardianRankingSummary,
					fileClient.getSignedUrl(guardianRankingSummary.getProfileKey()), false);
			})
			.toList()
		);
	}

	@Transactional(readOnly = true)
	public RakingSummaryListResponse getKidRankingSummaries(final Long memberId) {
		MemberSimpleResponse memberSimpleResponse = getMemberSimpleResponse(memberId);
		return new RakingSummaryListResponse(memberSimpleResponse, kidRankingSummaryRepository.findAll()
			.stream()
			.map(kidRankingSummary -> {
				if (kidRankingSummary.getNickname().equals(memberSimpleResponse.nickname())) {
					return KidRankingSummaryDto.of(kidRankingSummary,
						fileClient.getSignedUrl(kidRankingSummary.getProfileKey()), true);
				}
				return KidRankingSummaryDto.of(kidRankingSummary,
					fileClient.getSignedUrl(kidRankingSummary.getProfileKey()), false);
			})
			.toList()
		);
	}

	private MemberSimpleResponse getMemberSimpleResponse(final Long memberId) {
		Member member = memberRepository.findByIdWithMemberTypeDetail(memberId)
			.orElseThrow(() -> new NoSuchElementException(String.format("존재하지 않는 회원(%d)입니다.", memberId)));
		MemberPoint memberPoint = memberPointRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException(
			String.format("존재하지 않는 회원(%d)의 포인트 정보입니다.", memberId)));
		int myRanking = memberPointRepository.getPointRankingByMemberId(memberId);
		String profileUrl = fileClient.getSignedUrl(member.getProfileKey());
		return MemberSimpleResponse.from(member, memberPoint, profileUrl, myRanking);
	}
}
