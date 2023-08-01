package com.polzzak.domain.ranking.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polzzak.domain.ranking.dto.GuardianRankingSummaryDto;
import com.polzzak.domain.ranking.dto.KidRankingSummaryDto;
import com.polzzak.domain.ranking.dto.RakingSummaryListResponse;
import com.polzzak.domain.ranking.repository.GuardianRankingSummaryRepository;
import com.polzzak.domain.ranking.repository.KidRankingSummaryRepository;

@Service
public class RankingService {
	private final GuardianRankingSummaryRepository guardianRankingSummaryRepository;
	private final KidRankingSummaryRepository kidRankingSummaryRepository;

	public RankingService(final GuardianRankingSummaryRepository guardianRankingSummaryRepository,
		final KidRankingSummaryRepository kidRankingSummaryRepository) {
		this.guardianRankingSummaryRepository = guardianRankingSummaryRepository;
		this.kidRankingSummaryRepository = kidRankingSummaryRepository;
	}

	@Transactional(readOnly = true)
	public RakingSummaryListResponse getGuardianRankingSummaries() {
		return new RakingSummaryListResponse(guardianRankingSummaryRepository.findAll()
			.stream()
			.map(guardianRankingSummary -> GuardianRankingSummaryDto.from(guardianRankingSummary))
			.toList());
	}

	@Transactional(readOnly = true)
	public RakingSummaryListResponse getKidRankingSummaries() {
		return new RakingSummaryListResponse(kidRankingSummaryRepository.findAll()
			.stream()
			.map(kidRankingSummary -> KidRankingSummaryDto.from(kidRankingSummary))
			.toList());
	}
}
