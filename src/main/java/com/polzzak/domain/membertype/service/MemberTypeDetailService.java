package com.polzzak.domain.membertype.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polzzak.domain.membertype.dto.MemberTypeDetailDto;
import com.polzzak.domain.membertype.dto.MemberTypeDetailListDto;
import com.polzzak.domain.membertype.dto.MemberTypeDetailRequest;
import com.polzzak.domain.membertype.entity.MemberTypeDetail;
import com.polzzak.domain.membertype.repository.MemberTypeDetailRepository;

import jakarta.validation.Valid;

@Service
@Transactional(readOnly = true)
public class MemberTypeDetailService {

	private final MemberTypeDetailRepository memberTypeDetailRepository;

	public MemberTypeDetailService(final MemberTypeDetailRepository memberTypeDetailRepository) {
		this.memberTypeDetailRepository = memberTypeDetailRepository;
	}

	@Transactional
	public void saveMemberTypeDetail(final MemberTypeDetailRequest request) {
		validateExistDetail(request.detail());

		memberTypeDetailRepository.save(createMemberTypeDetail(request));
	}

	private MemberTypeDetail createMemberTypeDetail(final MemberTypeDetailRequest request) {
		return MemberTypeDetail.createMemberType()
			.memberType(request.memberType())
			.detail(request.detail())
			.build();
	}

	public MemberTypeDetail findMemberTypeDetailById(final Long id) {
		return memberTypeDetailRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("요청한 멤버 타입을 찾을 수 없습니다"));
	}

	public MemberTypeDetailListDto findMemberTypeDetailList() {
		return MemberTypeDetailListDto.from(
			memberTypeDetailRepository.findAll().stream()
				.map(memberTypeDetail -> MemberTypeDetailDto.from(memberTypeDetail))
				.toList()
		);
	}

	@Transactional
	public void deleteMemberTypeDetailById(final Long id) {
		memberTypeDetailRepository.deleteById(id);
	}

	@Transactional
	public void updateMemberTypeDetail(final Long id, final @Valid MemberTypeDetailRequest request) {
		validateExistDetail(request.detail());

		MemberTypeDetail memberTypeDetail = findMemberTypeDetailById(id);
		memberTypeDetail.update(request.memberType(), request.detail());
	}

	private void validateExistDetail(final String detail) {
		if (memberTypeDetailRepository.existsByDetail(detail)) {
			throw new IllegalArgumentException("이미 존재하는 타입입니다");
		}
	}
}
