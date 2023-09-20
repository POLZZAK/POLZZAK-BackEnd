package com.polzzak.support;

import java.util.List;

import com.polzzak.domain.ranking.dto.GuardianRankingSummaryDto;
import com.polzzak.domain.ranking.dto.KidRankingSummaryDto;
import com.polzzak.domain.ranking.dto.RakingSummaryListResponse;
import com.polzzak.domain.ranking.entity.RankingStatus;
import com.polzzak.domain.user.dto.MemberPointDto;
import com.polzzak.domain.user.dto.MemberSimpleResponse;
import com.polzzak.domain.user.dto.MemberTypeDto;

public class RankingFixtures {
	public static final RakingSummaryListResponse GUARDIAN_RANKING_SUMMARY_LIST_RESPONSE;
	public static final RakingSummaryListResponse KID_RANKING_SUMMARY_LIST_RESPONSE;
	public static final MemberSimpleResponse TEST_GUARDIAN_MEMBER_SIMPLE_RESPONSE;
	public static final MemberSimpleResponse TEST_KID_MEMBER_SIMPLE_RESPONSE;

	static {
		TEST_GUARDIAN_MEMBER_SIMPLE_RESPONSE = new MemberSimpleResponse(0L, "guardianNickname1",
			new MemberPointDto(300, 3),
			new MemberTypeDto("보호자", "삼촌"), "profileUrl1", 1);
		TEST_KID_MEMBER_SIMPLE_RESPONSE = new MemberSimpleResponse(0L, "kidNickname2", new MemberPointDto(200, 2),
			new MemberTypeDto("아이", "아이"), "profileUrl2", 2);
		GUARDIAN_RANKING_SUMMARY_LIST_RESPONSE = new RakingSummaryListResponse(TEST_GUARDIAN_MEMBER_SIMPLE_RESPONSE,
			List.of(
				new GuardianRankingSummaryDto(1, RankingStatus.UP, "guardianNickname1", 300, 3, "삼촌", "profileUrl1",
					true),
				new GuardianRankingSummaryDto(2, RankingStatus.UP, "guardianNickname2", 200, 2, "엄마", "profileUrl2",
					false)));
		KID_RANKING_SUMMARY_LIST_RESPONSE = new RakingSummaryListResponse(TEST_KID_MEMBER_SIMPLE_RESPONSE, List.of(
			new KidRankingSummaryDto(1, RankingStatus.UP, "kidNickname1", 300, 3, "profileUrl1", false),
			new KidRankingSummaryDto(2, RankingStatus.UP, "kidNickname2", 200, 2, "profileUrl2", true))
		);
	}
}
