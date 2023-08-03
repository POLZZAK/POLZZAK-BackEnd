package com.polzzak.domain.ranking.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polzzak.domain.ranking.dto.GuardianRankingSummaryDto;
import com.polzzak.domain.ranking.dto.KidRankingSummaryDto;
import com.polzzak.domain.ranking.dto.RakingSummaryListResponse;
import com.polzzak.domain.ranking.repository.GuardianRankingSummaryRepository;
import com.polzzak.domain.ranking.repository.KidRankingSummaryRepository;
import com.polzzak.global.infra.file.FileClient;

@Service
public class RankingService {
	private final GuardianRankingSummaryRepository guardianRankingSummaryRepository;
	private final KidRankingSummaryRepository kidRankingSummaryRepository;
	private final FileClient fileClient;

	public RankingService(final GuardianRankingSummaryRepository guardianRankingSummaryRepository,
		final KidRankingSummaryRepository kidRankingSummaryRepository, final FileClient fileClient) {
		this.guardianRankingSummaryRepository = guardianRankingSummaryRepository;
		this.kidRankingSummaryRepository = kidRankingSummaryRepository;
		this.fileClient = fileClient;
	}

	@Transactional(readOnly = true)
	public RakingSummaryListResponse getGuardianRankingSummaries() {
		return new RakingSummaryListResponse(guardianRankingSummaryRepository.findAll()
			.stream()
			.map(guardianRankingSummary -> GuardianRankingSummaryDto.of(guardianRankingSummary,
				fileClient.getSignedUrl(guardianRankingSummary.getProfileKey())))
			.toList());
	}

	@Transactional(readOnly = true)
	public RakingSummaryListResponse getKidRankingSummaries() {
		return new RakingSummaryListResponse(kidRankingSummaryRepository.findAll()
			.stream()
			.map(kidRankingSummary -> KidRankingSummaryDto.of(kidRankingSummary,
				fileClient.getSignedUrl(kidRankingSummary.getProfileKey())))
			.toList());
	}
}
