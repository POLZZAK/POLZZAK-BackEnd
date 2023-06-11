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
import com.polzzak.domain.family.entity.FamilyRequest;
import com.polzzak.domain.family.repository.FamilyMapRepository;
import com.polzzak.domain.family.repository.FamilyRequestRepository;
import com.polzzak.domain.user.entity.Member;
import com.polzzak.domain.user.service.UserService;
import com.polzzak.global.infra.file.FileClient;

@Service
@Transactional(readOnly = true)
public class FamilyMapService {
	private final FamilyMapRepository familyMapRepository;
	private final FamilyRequestRepository familyRequestRepository;

	private final FileClient fileClient;
	private final UserService userService;

	public FamilyMapService(final FamilyMapRepository familyMapRepository,
		final FamilyRequestRepository familyRequestRepository, final FileClient fileClient,
		final UserService userService) {
		this.familyMapRepository = familyMapRepository;
		this.familyRequestRepository = familyRequestRepository;
		this.fileClient = fileClient;
		this.userService = userService;
	}

	public SearchedMemberDto getSearchedMemberByNickname(final String username, final String nickname) {
		Member requestMember = userService.findMemberByUsername(username);

		Set<String> familiesNicknameSet = getFamilyMemberDtos(requestMember).stream()
			.map(familyMemberDto -> familyMemberDto.nickname())
			.collect(Collectors.toSet());

		Set<String> sentNicknameSet = getSentMemberDtos(requestMember.getId()).stream()
			.map(requestMemberDto -> requestMemberDto.nickname())
			.collect(Collectors.toSet());

		Set<String> receivedNicknameSet = getReceivedMemberDtos(requestMember.getId()).stream()
			.map(requestMemberDto -> requestMemberDto.nickname())
			.collect(Collectors.toSet());

		return userService.getMemberByNickname(username, nickname)
			.filter(findMember -> isValid(requestMember, findMember))
			.map(member -> SearchedMemberDto.from(member, fileClient.getSignedUrl(member.getProfileKey()),
				getFamilyState(member.getNickname(), familiesNicknameSet, sentNicknameSet, receivedNicknameSet)))
			.orElse(null);
	}

	@Transactional
	public void saveFamilyTempMap(final String username, final FamilyMapRequest familyMapRequest) {
		Member findMember = userService.findMemberByUsername(username);

		validateRequest(familyMapRequest, findMember);

		Member targetMember = userService.findMemberByMemberId(familyMapRequest.targetId());
		familyRequestRepository.save(createFamilyRequest(findMember, targetMember));
	}

	public List<FamilyMemberDto> getMyFamilies(final String username) {
		Member findMember = userService.findMemberByUsername(username);
		return getFamilyMemberDtos(findMember);
	}

	public List<FamilyMemberDto> getMyFamilies(final long memberId) {
		Member findMember = userService.findMemberByMemberId(memberId);
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
		familyRequestRepository.deleteBySenderIdAndReceiverId(targetId, findMember.getId());

		Member targetMember = userService.findMemberByMemberId(targetId);
		familyMapRepository.save(createFamilyMap(findMember, targetMember));
	}

	@Transactional
	public void deleteFamilyMap(final String username, final Long targetId) {
		Member findMember = userService.findMemberByUsername(username);
		long guardianId = findMember.getMemberType().isGuardianType() ? findMember.getId() : targetId;
		long kidId = findMember.getMemberType().isKidType() ? findMember.getId() : targetId;

		familyMapRepository.deleteByGuardianIdAndKidId(guardianId, kidId);
	}

	@Transactional
	public void rejectFamilyRequest(final String username, final Long targetId) {
		Member findMember = userService.findMemberByUsername(username);
		familyRequestRepository.deleteBySenderIdAndReceiverId(targetId, findMember.getId());
	}

	@Transactional
	public void cancelFamilyRequest(final String username, final Long targetId) {
		Member findMember = userService.findMemberByUsername(username);
		familyRequestRepository.deleteBySenderIdAndReceiverId(findMember.getId(), targetId);
	}

	public boolean isFamily(long guardianId, long kidId) {
		return familyMapRepository.existsByGuardianIdAndKidId(guardianId, kidId);
	}

	private void validateRequest(final FamilyMapRequest familyMapRequest, final Member findMember) {
		if (familyRequestRepository.existsBySenderAndReceiverId(findMember.getId(), familyMapRequest.targetId())
			.isPresent()
			|| familyRequestRepository.existsBySenderAndReceiverId(familyMapRequest.targetId(), findMember.getId())
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

	private FamilyRequest createFamilyRequest(final Member sender, final Member receiver) {
		return FamilyRequest.createFamilyRequest()
			.sender(sender)
			.receiver(receiver)
			.build();
	}

	private FamilyMap createFamilyMap(final Member member, final Member targetMember) {
		return FamilyMap.createFamilyMap()
			.guardian(member.isGuardian() ? member : targetMember)
			.kid(member.isKid() ? member : targetMember)
			.build();
	}

	private List<FamilyMemberDto> getFamilyMemberDtos(final Member findMember) {
		if (findMember.isKid()) {
			return getGuardianDtoList(familyMapRepository.getFamilyMapsByKidId(findMember.getId()));
		}

		return getKidDtoList(familyMapRepository.getFamilyMapsByGuardianId(findMember.getId()));
	}

	private List<FamilyMemberDto> getKidDtoList(final List<FamilyMap> familyMaps) {
		return familyMaps.stream().map(familyMap -> {
			Member kid = familyMap.getKid();
			return FamilyMemberDto.from(kid, fileClient.getSignedUrl(kid.getProfileKey()));
		}).toList();
	}

	private List<FamilyMemberDto> getGuardianDtoList(final List<FamilyMap> familyMaps) {
		return familyMaps.stream().map(familyMap -> {
			Member guardian = familyMap.getGuardian();
			return FamilyMemberDto.from(guardian, fileClient.getSignedUrl(guardian.getProfileKey()));
		}).toList();
	}

	private List<FamilyMemberDto> getSentMemberDtos(final long memberId) {
		List<FamilyRequest> familyRequests = familyRequestRepository.findAllBySenderId(memberId);

		return familyRequests.stream()
			.map(familyRequest -> FamilyMemberDto.from(familyRequest.getReceiver(),
				fileClient.getSignedUrl(familyRequest.getReceiver().getProfileKey())))
			.toList();
	}

	private List<FamilyMemberDto> getReceivedMemberDtos(final long memberId) {
		List<FamilyRequest> familyRequests = familyRequestRepository.findAllByReceiverId(memberId);

		return familyRequests.stream()
			.map(familyRequest -> FamilyMemberDto.from(familyRequest.getSender(),
				fileClient.getSignedUrl(familyRequest.getSender().getProfileKey())))
			.toList();
	}

	private boolean isValid(final Member requestMember, final Member findMember) {
		if ((requestMember.isKid() && findMember.isKid())
			|| (requestMember.isGuardian() && findMember.isGuardian())
		) {
			return false;
		}

		return true;
	}
}
