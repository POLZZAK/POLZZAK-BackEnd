package com.polzzak.domain.ranking.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.polzzak.domain.ranking.repository.GuardianRankingSummaryRepository;
import com.polzzak.domain.ranking.repository.KidRankingSummaryRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RankingScheduler {

	public static final int DEFAULT_PAGE_SIZE = 0;
	public static final int RANKING_DEFAULT_SIZE = 30;
	private final GuardianRankingSummaryRepository guardianRankingSummaryRepository;
	private final KidRankingSummaryRepository kidRankingSummaryRepository;
	private final MemberPointRepository memberPointRepository;

	public RankingScheduler(final GuardianRankingSummaryRepository guardianRankingSummaryRepository,
		final KidRankingSummaryRepository kidRankingSummaryRepository,
		final MemberPointRepository memberPointRepository) {
		this.guardianRankingSummaryRepository = guardianRankingSummaryRepository;
		this.kidRankingSummaryRepository = kidRankingSummaryRepository;
		this.memberPointRepository = memberPointRepository;
	}

	@Transactional
	@Scheduled(cron = "0 0 20 * * *", zone = "Asia/Seoul")
	public void rankingUpdateTask() {
		log.info("Ranking Update Task Start");
		PageRequest top30PageRequest = PageRequest.of(DEFAULT_PAGE_SIZE, RANKING_DEFAULT_SIZE);
		List<GuardianRankingSummary> guardianRankingSummaries = getNewGuardianRankingSummaries(top30PageRequest);
		List<KidRankingSummary> kidRankingSummaries = getNewKidRankingSummaries(top30PageRequest);
		guardianRankingSummaryRepository.bulkDeleteAllData();
		kidRankingSummaryRepository.bulkDeleteAllData();
		guardianRankingSummaryRepository.saveAll(guardianRankingSummaries);
		kidRankingSummaryRepository.saveAll(kidRankingSummaries);
		log.info("Ranking Update Task End");
	}

	private List<GuardianRankingSummary> getNewGuardianRankingSummaries(final Pageable pageable) {
		Map<String, GuardianRankingSummary> guardianRankingSummaryMap = getGuardianRankingSummaryMap();
		List<MemberPoint> guardianMemberPoints = getMemberPointRankings(MemberType.GUARDIAN, pageable);
		RankingPosition rankingPosition = new RankingPosition();
		List<GuardianRankingSummary> newGuardianRankingSummaries = guardianMemberPoints.stream()
			.map(guardianMemberPoint -> {
				rankingPosition.updateRankingPosition(guardianMemberPoint.getPoint());
				String nickname = guardianMemberPoint.getMember().getNickname();
				if (guardianRankingSummaryMap.containsKey(nickname)) {
					GuardianRankingSummary guardianRankingSummary = guardianRankingSummaryMap.get(nickname);
					RankingStatus rankingStatus = RankingStatus.getValue(guardianRankingSummary.getRanking(),
						rankingPosition.getRanking());
					return getGuardianRankingSummary(rankingPosition.getRanking(), rankingStatus, guardianMemberPoint);
				}
				return getGuardianRankingSummary(rankingPosition.getRanking(), RankingStatus.UP, guardianMemberPoint);
			})
			.toList();
		return sortNewGuardianRankingSummaries(newGuardianRankingSummaries);
	}

	private List<KidRankingSummary> getNewKidRankingSummaries(final Pageable pageable) {
		Map<String, KidRankingSummary> kidRankingSummaryMap = getKidRankingSummaryMap();
		List<MemberPoint> kidMemberPoints = getMemberPointRankings(MemberType.KID, pageable);
		RankingPosition rankingPosition = new RankingPosition();
		List<KidRankingSummary> newKidRankingSummaries = kidMemberPoints.stream().map(kidMemberPoint -> {
			rankingPosition.updateRankingPosition(kidMemberPoint.getPoint());
			String nickname = kidMemberPoint.getMember().getNickname();
			if (kidRankingSummaryMap.containsKey(nickname)) {
				KidRankingSummary kidRankingSummary = kidRankingSummaryMap.get(nickname);
				RankingStatus rankingStatus = RankingStatus.getValue(kidRankingSummary.getRanking(),
					rankingPosition.getRanking());
				return getKidRankingSummary(rankingPosition.getRanking(), rankingStatus, kidMemberPoint);
			}
			return getKidRankingSummary(rankingPosition.getRanking(), RankingStatus.UP, kidMemberPoint);
		}).toList();
		return sortNewKidRankingSummaries(newKidRankingSummaries);
	}

	private List<GuardianRankingSummary> sortNewGuardianRankingSummaries(
		final List<GuardianRankingSummary> newGuardianRankingSummaries) {
		return newGuardianRankingSummaries.stream().sorted((o1, o2) -> {
			if (o1.getRanking() == o2.getRanking()) {
				return o1.getMemberPoint().getMember().getId().compareTo(o2.getMemberPoint().getMember().getId());
			}
			return o1.getRanking() - o2.getRanking();
		}).toList();
	}

	private List<KidRankingSummary> sortNewKidRankingSummaries(final List<KidRankingSummary> newKidRankingSummaries) {
		return newKidRankingSummaries.stream().sorted((o1, o2) -> {
			if (o1.getRanking() == o2.getRanking()) {
				return o1.getMemberPoint().getMember().getId().compareTo(o2.getMemberPoint().getMember().getId());
			}
			return o1.getRanking() - o2.getRanking();
		}).toList();
	}

	private GuardianRankingSummary getGuardianRankingSummary(final int ranking, final RankingStatus rankingStatus,
		final MemberPoint guardianMemberPoint) {
		return GuardianRankingSummary.createGuardianRankingSummary()
			.ranking(ranking)
			.rankingStatus(rankingStatus)
			.nickname(guardianMemberPoint.getMember().getNickname())
			.point(guardianMemberPoint.getPoint())
			.level(guardianMemberPoint.getLevel())
			.profileKey(guardianMemberPoint.getMember().getProfileKey())
			.memberTypeDetail(guardianMemberPoint.getMember().getMemberType().getDetail())
			.memberPoint(guardianMemberPoint)
			.build();
	}

	private KidRankingSummary getKidRankingSummary(final int ranking, final RankingStatus rankingStatus,
		final MemberPoint kidMemberPoint) {
		return KidRankingSummary.createKidRankingSummary()
			.ranking(ranking)
			.rankingStatus(rankingStatus)
			.nickname(kidMemberPoint.getMember().getNickname())
			.point(kidMemberPoint.getPoint())
			.level(kidMemberPoint.getLevel())
			.profileKey(kidMemberPoint.getMember().getProfileKey())
			.memberPoint(kidMemberPoint)
			.build();
	}

	private Map<String, GuardianRankingSummary> getGuardianRankingSummaryMap() {
		return guardianRankingSummaryRepository.findAll()
			.stream()
			.collect(Collectors.toMap(it -> it.getNickname(), it -> it));
	}

	private Map<String, KidRankingSummary> getKidRankingSummaryMap() {
		return kidRankingSummaryRepository.findAll()
			.stream()
			.collect(Collectors.toMap(it -> it.getNickname(), it -> it));
	}

	private List<MemberPoint> getMemberPointRankings(final MemberType memberType,
		final Pageable pageable) {
		return memberPointRepository.findMemberPointRankings(memberType, pageable);
	}
}
