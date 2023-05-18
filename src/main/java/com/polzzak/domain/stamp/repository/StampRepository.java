package com.polzzak.domain.stamp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.polzzak.domain.stamp.entity.Stamp;

public interface StampRepository extends JpaRepository<Stamp, Long> {
}
