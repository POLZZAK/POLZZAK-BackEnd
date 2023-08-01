package com.polzzak.domain.ranking.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.polzzak.domain.memberpoint.repository.MemberPointRepository;
import com.polzzak.domain.ranking.entity.GuardianRanking;
import com.polzzak.domain.ranking.entity.KidRanking;
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

	@Scheduled(cron = "* * 20 * * *") // 오후 8시
	public void rankingTask() {
		List<GuardianRanking> guardianRankings = guardianRankingRepository.findAll();
		List<KidRanking> kidRankings = kidRankingRepository.findAll();
	}
}
