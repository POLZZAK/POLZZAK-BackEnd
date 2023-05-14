package com.polzzak.domain.family.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polzzak.domain.family.dto.FamilyMapRequest;
import com.polzzak.domain.family.dto.FamilyMemberDto;
import com.polzzak.domain.family.dto.FamilyStatus;
import com.polzzak.domain.family.dto.SearchedMemberDto;
import com.polzzak.domain.family.entity.FamilyMap;
import com.polzzak.domain.family.entity.FamilyTempMap;
import com.polzzak.domain.family.repository.FamilyMapRepository;
import com.polzzak.domain.family.repository.FamilyTempMapRepository;
import com.polzzak.domain.user.entity.Member;
import com.polzzak.domain.user.entity.MemberType;
import com.polzzak.domain.user.service.UserService;
import com.polzzak.global.infra.file.FileClient;

@Service
@Transactional(readOnly = true)
public class FamilyMapService {
	private final FamilyMapRepository familyMapRepository;
	private final FamilyTempMapRepository familyTempMapRepository;

	private final FileClient fileClient;
	private final UserService userService;

	public FamilyMapService(final FamilyMapRepository familyMapRepository,
		final FamilyTempMapRepository familyTempMapRepository, final FileClient fileClient,
		final UserService userService) {
		this.familyMapRepository = familyMapRepository;
		this.familyTempMapRepository = familyTempMapRepository;
		this.fileClient = fileClient;
		this.userService = userService;
	}

	public List<SearchedMemberDto> getSearchedMemberByNickname(final String username, final String nickname) {
		Member findMember = userService.findMemberByUsername(username);

		Set<String> familiesNicknameSet = getFamilyMemberDtos(findMember).stream()
			.map(familyMemberDto -> familyMemberDto.nickname())
			.collect(Collectors.toSet());

		Set<String> sentNicknameSet = getSentMemberDtos(findMember.getId()).stream()
			.map(requestMemberDto -> requestMemberDto.nickname())
			.collect(Collectors.toSet());

		Set<String> receivedNicknameSet = getReceivedMemberDtos(findMember.getId()).stream()
			.map(requestMemberDto -> requestMemberDto.nickname())
			.collect(Collectors.toSet());

		return userService.getMemberByNickname(username, nickname)
			.stream()
			.filter(memberDto -> SearchFilter(findMember.getMemberType(), memberDto.memberType()))
			.map(memberDto -> SearchedMemberDto.from(memberDto,
				getFamilyState(memberDto.nickname(), familiesNicknameSet, sentNicknameSet, receivedNicknameSet)))
			.toList();
	}

	@Transactional
	public void saveFamilyTempMap(final String username, final FamilyMapRequest familyMapRequest) {
		Member findMember = userService.findMemberByUsername(username);

		validateRequest(familyMapRequest, findMember);

		familyTempMapRepository.save(createFamilyTempMap(findMember.getId(), familyMapRequest.targetId()));
	}

	public List<FamilyMemberDto> getMyFamilies(final String username) {
		Member findMember = userService.findMemberByUsername(username);
		return getFamilyMemberDtos(findMember);
	}

	public List<FamilyMemberDto> getMySentList(final String username) {
		long memberId = userService.findMemberByUsername(username).getId();
		return getSentMemberDtos(memberId);
	}

	public List<FamilyMemberDto> getMyReceivedList(final String username) {
		long memberId = userService.findMemberByUsername(username).getId();
		return getReceivedMemberDtos(memberId);
	}

	@Transactional
	public void approveFamilyMap(final String username, final Long targetId) {
		Member findMember = userService.findMemberByUsername(username);
		familyTempMapRepository.deleteBySenderIdAndReceiverId(targetId, findMember.getId());
		familyMapRepository.save(createFamilyMap(findMember, targetId));
	}

	@Transactional
	public void rejectFamilyMap(final String username, final Long targetId) {
		Member findMember = userService.findMemberByUsername(username);
		familyTempMapRepository.deleteBySenderIdAndReceiverId(targetId, findMember.getId());
	}

	@Transactional
	public void cancelFamilyMap(final String username, final Long targetId) {
		Member findMember = userService.findMemberByUsername(username);
		familyTempMapRepository.deleteBySenderIdAndReceiverId(findMember.getId(), targetId);
	}

	private void validateRequest(final FamilyMapRequest familyMapRequest, final Member findMember) {
		if (familyTempMapRepository.existsBySenderAndReceiverId(findMember.getId(), familyMapRequest.targetId())
			.isPresent()
			|| familyTempMapRepository.existsBySenderAndReceiverId(familyMapRequest.targetId(), findMember.getId())
			.isPresent()) {
			throw new IllegalArgumentException("중복된 요청입니다");
		}
	}

	private FamilyStatus getFamilyState(final String nickname, final Set<String> familiesNicknameSet,
		final Set<String> sentNicknameSet, final Set<String> receivedNicknameSet) {
		if (familiesNicknameSet.contains(nickname)) {
			return FamilyStatus.APPROVE;
		}

		if (sentNicknameSet.contains(nickname)) {
			return FamilyStatus.SENT;
		}

		if (receivedNicknameSet.contains(nickname)) {
			return FamilyStatus.RECEIVED;
		}

		return FamilyStatus.NONE;
	}

	private FamilyTempMap createFamilyTempMap(final Long senderId, final Long targetId) {
		return FamilyTempMap.createFamilyTempMap()
			.senderId(senderId)
			.receiverId(targetId)
			.build();
	}

	private FamilyMap createFamilyMap(final Member member, final Long targetId) {
		return FamilyMap.createFamilyMap()
			.guardianId(member.getMemberType() == MemberType.KID ? targetId : member.getId())
			.kidId(member.getMemberType() == MemberType.KID ? member.getId() : targetId)
			.build();
	}

	private List<FamilyMemberDto> getFamilyMemberDtos(final Member findMember) {
		if (findMember.getMemberType() == MemberType.KID) {
			return getGuardianDtoList(familyMapRepository.getFamilyMapsByKidId(findMember.getId()));
		}

		return getKidDtoList(familyMapRepository.getFamilyMapsByGuardianId(findMember.getId()));
	}

	private List<FamilyMemberDto> getKidDtoList(final List<FamilyMap> familyMaps) {
		return familyMaps.stream().map(familyMap -> {
			Member kid = userService.findMemberByMemberId(familyMap.getKidId());
			return FamilyMemberDto.from(kid, fileClient.getSignedUrl(kid.getProfileKey()));
		}).toList();
	}

	private List<FamilyMemberDto> getGuardianDtoList(final List<FamilyMap> familyMaps) {
		return familyMaps.stream().map(familyMap -> {
			Member guardian = userService.findMemberByMemberId(familyMap.getGuardianId());
			return FamilyMemberDto.from(guardian, fileClient.getSignedUrl(guardian.getProfileKey()));
		}).toList();
	}

	private List<FamilyMemberDto> getSentMemberDtos(final long memberId) {
		List<FamilyTempMap> familyTempMaps = familyTempMapRepository.findAllBySenderId(memberId);

		return familyTempMaps.stream()
			.map(familyTempMap -> getFamilyMemberDto(familyTempMap.getReceiverId()))
			.toList();
	}

	private List<FamilyMemberDto> getReceivedMemberDtos(final long memberId) {
		List<FamilyTempMap> familyTempMaps = familyTempMapRepository.findAllByReceiverId(memberId);

		return familyTempMaps.stream()
			.map(familyTempMap -> getFamilyMemberDto(familyTempMap.getSenderId()))
			.toList();
	}

	private FamilyMemberDto getFamilyMemberDto(final long memberId) {
		Member findMember = userService.findMemberByMemberId(memberId);
		return FamilyMemberDto.from(findMember, fileClient.getSignedUrl(findMember.getProfileKey()));
	}

	private boolean SearchFilter(final MemberType findMemberType, final MemberType searchedMemberType) {
		if ((findMemberType == MemberType.KID && searchedMemberType == MemberType.KID)
			|| (findMemberType != MemberType.KID && searchedMemberType != MemberType.KID)
		) {
			return false;
		}

		return true;
	}
}
