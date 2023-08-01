package com.polzzak.domain.ranking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.polzzak.domain.ranking.entity.KidRankingSummary;

@Repository
public interface KidRankingRepository extends JpaRepository<KidRankingSummary, Long> {
}
