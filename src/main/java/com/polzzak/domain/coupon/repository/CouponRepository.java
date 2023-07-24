package com.polzzak.domain.coupon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.polzzak.domain.coupon.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

	List<Coupon> findByGuardianIdAndState(Long guardianId, Coupon.CouponState state);

	List<Coupon> findByKidIdAndState(Long kidId, Coupon.CouponState state);
}
