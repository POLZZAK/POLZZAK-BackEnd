package com.polzzak.domain.ranking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.polzzak.domain.ranking.entity.GuardianRankingSummary;

@Repository
public interface GuardianRankingSummaryRepository extends JpaRepository<GuardianRankingSummary, Long> {
	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Query("DELETE FROM GuardianRankingSummary")
	void bulkDeleteAllData();
}
