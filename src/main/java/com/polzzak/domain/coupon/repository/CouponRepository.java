package com.polzzak.domain.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.polzzak.domain.coupon.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
