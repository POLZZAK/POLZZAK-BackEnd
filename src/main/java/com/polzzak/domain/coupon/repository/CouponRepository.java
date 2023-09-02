package com.polzzak.domain.coupon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.polzzak.domain.coupon.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

	List<Coupon> findByGuardianIdAndState(final Long guardianId, final Coupon.CouponState state);

	List<Coupon> findByKidIdAndState(final Long kidId, final Coupon.CouponState state);

	List<Coupon> findByState(final Coupon.CouponState state);
}
