package com.polzzak.domain.memberpoint.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polzzak.domain.memberpoint.dto.MemberPointHistorySliceResponse;
import com.polzzak.domain.memberpoint.dto.MemberPointResponse;
import com.polzzak.domain.memberpoint.entity.MemberPoint;
import com.polzzak.domain.memberpoint.entity.MemberPointHistory;
import com.polzzak.domain.memberpoint.entity.MemberPointType;
import com.polzzak.domain.memberpoint.repository.MemberPointHistoryRepository;
import com.polzzak.domain.memberpoint.repository.MemberPointRepository;
import com.polzzak.domain.user.entity.Member;

@Service
public class MemberPointService {
	private final MemberPointRepository memberPointRepository;
	private final MemberPointHistoryRepository memberPointHistoryRepository;

	public MemberPointService(final MemberPointRepository memberPointRepository,
		final MemberPointHistoryRepository memberPointHistoryRepository) {
		this.memberPointRepository = memberPointRepository;
		this.memberPointHistoryRepository = memberPointHistoryRepository;
	}

	@Transactional
	public void saveMemberPoint(final Member member) {
		final MemberPoint memberPoint = createMemberPoint(member);
		MemberPoint savedMemberPoint = memberPointRepository.save(memberPoint);
		memberPointHistoryRepository.save(createMemberRegistrationPointHistory(savedMemberPoint));
	}

	@Transactional
	public MemberPointResponse getMyMemberPoint(final long id) {
		MemberPoint memberPoint = getMemberPointWithWriteLock(id);
		return MemberPointResponse.from(memberPoint);
	}

	public MemberPointHistorySliceResponse getMyEarningHistories(final long memberId, final long startId,
		final int size) {
		PageRequest pageRequest = PageRequest.of(0, size, Sort.by("id").descending());
		Slice<MemberPointHistory> pointHistorySlice = getPointHistorySlice(startId, memberId, pageRequest);
		return MemberPointHistorySliceResponse.from(pointHistorySlice, pageRequest);
	}

	@Transactional
	public MemberPoint getMemberPointWithWriteLock(final long memberId) {
		return memberPointRepository.findWithWriteLockByMemberId(memberId)
			.orElseThrow(() -> new IllegalArgumentException(String.format("사용자 %d의 포인트가 존재하지 않습니다", memberId)));
	}

	@Transactional
	public void saveMemberPointHistory(final MemberPointHistory memberPointHistory) {
		memberPointHistoryRepository.save(memberPointHistory);
	}

	private MemberPointHistory createMemberRegistrationPointHistory(final MemberPoint savedMemberPoint) {
		return MemberPointHistory.createMemberPointHistory()
			.description(MemberPointType.REGISTER.getDescription())
			.increasedPoint(MemberPointType.REGISTER.getIncreasedPoint())
			.remainingPoint(savedMemberPoint.getPoint())
			.memberPoint(savedMemberPoint)
			.build();
	}

	private MemberPoint createMemberPoint(final Member member) {
		return MemberPoint.createMemberPoint().member(member).build();
	}

	private Slice<MemberPointHistory> getPointHistorySlice(final long startId, final long memberId,
		final PageRequest pageRequest) {
		if (startId == 0) {
			return memberPointHistoryRepository.findSliceByMemberPointMemberId(memberId, pageRequest);
		}
		return memberPointHistoryRepository.findSliceByMemberPointMemberIdAndIdLessThan(memberId, startId, pageRequest);
	}
}
