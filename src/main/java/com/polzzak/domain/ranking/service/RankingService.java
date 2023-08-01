package com.polzzak.domain.ranking.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polzzak.domain.memberpoint.entity.MemberPoint;
import com.polzzak.domain.memberpoint.repository.MemberPointRepository;
import com.polzzak.domain.membertype.entity.MemberType;
import com.polzzak.domain.ranking.entity.GuardianRankingSummary;
import com.polzzak.domain.ranking.entity.KidRankingSummary;
import com.polzzak.domain.ranking.entity.RankingStatus;
import com.polzzak.domain.ranking.repository.GuardianRankingRepository;
import com.polzzak.domain.ranking.repository.KidRankingRepository;

@Service
public class RankingService {

	private final GuardianRankingRepository guardianRankingRepository;
	private final KidRankingRepository kidRankingRepository;
	private final MemberPointRepository memberPointRepository;

	public RankingService(final GuardianRankingRepository guardianRankingRepository,
		final KidRankingRepository kidRankingRepository, final MemberPointRepository memberPointRepository) {
		this.guardianRankingRepository = guardianRankingRepository;
		this.kidRankingRepository = kidRankingRepository;
		this.memberPointRepository = memberPointRepository;
	}

	@Transactional
	@Scheduled(cron = "* * 20 * * *") // 오후 8시
	public void rankingUpdateTask() {
		PageRequest top30PageRequest = PageRequest.of(0, 30);
		List<GuardianRankingSummary> guardianRankingSummaries = getGuardianRankingSummaries(top30PageRequest);
		List<KidRankingSummary> kidRankingSummaries = getKidRankingSummaries(top30PageRequest);
		guardianRankingRepository.deleteAll();
		kidRankingRepository.deleteAll();
		guardianRankingRepository.saveAll(guardianRankingSummaries);
		kidRankingRepository.saveAll(kidRankingSummaries);
	}

	private List<GuardianRankingSummary> getGuardianRankingSummaries(final Pageable pageable) {
		List<MemberPoint> guardianMemberPoints = getMemberPointRankings(MemberType.GUARDIAN, pageable);
		Map<String, GuardianRankingSummary> guardianRankingSummaryMap = getGuardianRankingSummaryMap();
		return IntStream.range(1, guardianMemberPoints.size())
			.mapToObj(ranking -> {
				MemberPoint guardianMemberPoint = guardianMemberPoints.get(ranking - 1);
				String nickname = guardianMemberPoint.getMember().getNickname();
				if (guardianRankingSummaryMap.containsKey(nickname)) {
					GuardianRankingSummary guardianRankingSummary = guardianRankingSummaryMap.get(nickname);
					RankingStatus rankingStatus = RankingStatus.getValue(guardianRankingSummary.getRanking(), ranking);
					return getGuardianRankingSummary(ranking, rankingStatus, guardianMemberPoint);
				}
				return getGuardianRankingSummary(ranking, RankingStatus.UP, guardianMemberPoint);
			})
			.collect(Collectors.toList());
	}

	private List<KidRankingSummary> getKidRankingSummaries(final Pageable pageable) {
		List<MemberPoint> kidMemberPoints = getMemberPointRankings(MemberType.KID, pageable);
		Map<String, KidRankingSummary> kidRankingSummaryMap = getKidRankingSummaryMap();
		return IntStream.range(1, kidMemberPoints.size())
			.mapToObj(ranking -> {
				MemberPoint kidMemberPoint = kidMemberPoints.get(ranking - 1);
				String nickname = kidMemberPoint.getMember().getNickname();
				if (kidRankingSummaryMap.containsKey(nickname)) {
					KidRankingSummary kidRankingSummary = kidRankingSummaryMap.get(nickname);
					RankingStatus rankingStatus = RankingStatus.getValue(kidRankingSummary.getRanking(), ranking);
					return getKidRankingSummary(ranking, rankingStatus, kidMemberPoint);
				}
				return getKidRankingSummary(ranking, RankingStatus.UP, kidMemberPoint);
			})
			.collect(Collectors.toList());
	}

	private GuardianRankingSummary getGuardianRankingSummary(final int ranking, final RankingStatus rankingStatus,
		final MemberPoint memberPoint) {
		return GuardianRankingSummary.createGuardianRankingSummary()
			.ranking(ranking)
			.rankingStatus(rankingStatus)
			.nickname(memberPoint.getMember().getNickname())
			.point(memberPoint.getPoint())
			.level(memberPoint.getLevel())
			.memberTypeDetail(memberPoint.getMember().getMemberType().getDetail())
			.build();
	}

	private KidRankingSummary getKidRankingSummary(final int ranking, final RankingStatus rankingStatus,
		final MemberPoint memberPoint) {
		return KidRankingSummary.createKidRankingSummary()
			.ranking(ranking)
			.rankingStatus(rankingStatus)
			.nickname(memberPoint.getMember().getNickname())
			.point(memberPoint.getPoint())
			.level(memberPoint.getLevel())
			.build();
	}

	private Map<String, GuardianRankingSummary> getGuardianRankingSummaryMap() {
		return guardianRankingRepository.findAll()
			.stream()
			.collect(Collectors.toMap(it -> it.getNickname(), it -> it));
	}

	private Map<String, KidRankingSummary> getKidRankingSummaryMap() {
		return kidRankingRepository.findAll()
			.stream()
			.collect(Collectors.toMap(it -> it.getNickname(), it -> it));
	}

	private List<MemberPoint> getMemberPointRankings(final MemberType memberType,
		final Pageable pageable) {
		return memberPointRepository.findMemberPointRankings(memberType, pageable);
	}
}
