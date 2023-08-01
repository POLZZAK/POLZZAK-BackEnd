package com.polzzak.domain.ranking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.polzzak.domain.ranking.entity.GuardianRanking;

@Repository
public interface GuardianRankingRepository extends JpaRepository<GuardianRanking, Long> {
}
