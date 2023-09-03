package com.polzzak.domain.family.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polzzak.domain.family.dto.FamilyMapRequest;
import com.polzzak.domain.family.dto.FamilyMemberDto;
import com.polzzak.domain.family.dto.FamilyNewRequestMarkDto;
import com.polzzak.domain.family.dto.FamilyStatus;
import com.polzzak.domain.family.dto.SearchedMemberDto;
import com.polzzak.domain.family.entity.FamilyMap;
import com.polzzak.domain.family.entity.FamilyMapCreatedEvent;
import com.polzzak.domain.family.entity.FamilyRequest;
import com.polzzak.domain.family.repository.FamilyMapRepository;
import com.polzzak.domain.family.repository.FamilyRequestRepository;
import com.polzzak.domain.notification.dto.NotificationCreateEvent;
import com.polzzak.domain.notification.entity.NotificationType;
import com.polzzak.domain.user.entity.Member;
import com.polzzak.domain.user.repository.MemberRepository;
import com.polzzak.global.infra.file.FileClient;

@Service
@Transactional(readOnly = true)
public class FamilyMapService {
	private final FamilyMapRepository familyMapRepository;
	private final FamilyRequestRepository familyRequestRepository;

	private final FileClient fileClient;
	private final MemberRepository memberRepository;
	private final ApplicationEventPublisher eventPublisher;

	public FamilyMapService(final FamilyMapRepository familyMapRepository,
		final FamilyRequestRepository familyRequestRepository, final FileClient fileClient,
		final MemberRepository memberRepository, final ApplicationEventPublisher eventPublisher) {
		this.familyMapRepository = familyMapRepository;
		this.familyRequestRepository = familyRequestRepository;
		this.fileClient = fileClient;
		this.memberRepository = memberRepository;
		this.eventPublisher = eventPublisher;
	}

	public SearchedMemberDto getSearchedMemberByNickname(final long memberId, final String nickname) {
		Member requestMember = findMemberByMemberIdWithMemberType(memberId);

		Set<String> familiesNicknameSet = getFamilyMemberDtos(requestMember).stream()
			.map(familyMemberDto -> familyMemberDto.nickname())
			.collect(Collectors.toSet());

		Set<String> sentNicknameSet = getSentMemberDtos(requestMember.getId()).stream()
			.map(requestMemberDto -> requestMemberDto.nickname())
			.collect(Collectors.toSet());

		Set<String> receivedNicknameSet = getReceivedMemberDtos(requestMember.getId()).stream()
			.map(requestMemberDto -> requestMemberDto.nickname())
			.collect(Collectors.toSet());

		return getMemberByNickname(requestMember, nickname)
			.filter(findMember -> isValid(requestMember, findMember))
			.map(member -> SearchedMemberDto.from(member, fileClient.getSignedUrl(member.getProfileKey()),
				getFamilyState(member.getNickname(), familiesNicknameSet, sentNicknameSet, receivedNicknameSet)))
			.orElse(null);
	}

	@Transactional
	public void saveFamilyTempMap(final long memberId, final FamilyMapRequest familyMapRequest) {
		Member requestMember = findMemberByMemberIdWithMemberType(memberId);
		validateSaveFamilyRequest(requestMember, familyMapRequest.targetId());
		Member targetMember = findMemberByMemberId(familyMapRequest.targetId());
		familyRequestRepository.save(createFamilyRequest(requestMember, targetMember));
		eventPublisher.publishEvent(
			new NotificationCreateEvent(requestMember.getId(), targetMember.getId(), NotificationType.FAMILY_REQUEST,
				null));
	}

	public List<FamilyMemberDto> getMyFamilies(final long memberId) {
		Member findMember = findMemberByMemberIdWithMemberType(memberId);
		return getFamilyMemberDtos(findMember);
	}

	public List<FamilyMemberDto> getMySentList(final long memberId) {
		return getSentMemberDtos(memberId);
	}

	public List<FamilyMemberDto> getMyReceivedList(final long memberId) {
		return getReceivedMemberDtos(memberId);
	}

	public FamilyNewRequestMarkDto getFamilyNewRequestMark(final long memberId) {
		Member requestMember = findMemberByMemberId(memberId);
		boolean isFamilyReceived = familyRequestRepository.existsByReceiver(requestMember);
		boolean isFamilySent = familyRequestRepository.existsBySender(requestMember);
		return new FamilyNewRequestMarkDto(isFamilyReceived, isFamilySent);
	}

	@Transactional
	public void approveFamilyMap(final long memberId, final Long targetId) {
		Member requestMember = findMemberByMemberId(memberId);
		familyRequestRepository.deleteBySenderIdAndReceiverId(targetId, memberId);
		validateDuplicateFamilyMap(requestMember, targetId);
		Member targetMember = findMemberByMemberId(targetId);
		familyMapRepository.save(createFamilyMap(requestMember, targetMember));
		eventPublisher.publishEvent(new FamilyMapCreatedEvent(List.of(requestMember, targetMember)));
		eventPublisher.publishEvent(
			new NotificationCreateEvent(memberId, targetId, NotificationType.FAMILY_REQUEST_COMPLETE, null));
	}

	@Transactional
	public void deleteFamilyMap(final long memberId, final Long targetId) {
		Member requestMember = findMemberByMemberIdWithMemberType(memberId);
		long guardianId = requestMember.getMemberType().isGuardianType() ? requestMember.getId() : targetId;
		long kidId = requestMember.getMemberType().isKidType() ? requestMember.getId() : targetId;
		familyMapRepository.deleteByGuardianIdAndKidId(guardianId, kidId);
	}

	@Transactional
	public void rejectFamilyRequest(final long memberId, final Long targetId) {
		familyRequestRepository.deleteBySenderIdAndReceiverId(targetId, memberId);
		eventPublisher.publishEvent(
			new NotificationCreateEvent(memberId, targetId, NotificationType.FAMILY_REQUEST_REJECT, null));
	}

	@Transactional
	public void cancelFamilyRequest(final long memberId, final Long targetId) {
		familyRequestRepository.deleteBySenderIdAndReceiverId(memberId, targetId);
	}

	public boolean isFamily(long guardianId, long kidId) {
		return familyMapRepository.existsByGuardianIdAndKidId(guardianId, kidId);
	}

	private void validateSaveFamilyRequest(final Member requestMember, final long targetId) {
		validateDuplicateFamilyRequest(requestMember, targetId);
		validateDuplicateFamilyMap(requestMember, targetId);
	}

	private void validateDuplicateFamilyRequest(final Member requestMember, final long targetId) {
		if (familyRequestRepository.existsBySenderIdAndReceiverId(targetId, requestMember.getId())
			|| familyRequestRepository.existsBySenderIdAndReceiverId(requestMember.getId(), targetId)
		) {
			throw new IllegalArgumentException("중복된 요청입니다");
		}
	}

	private void validateDuplicateFamilyMap(final Member requestMember, final long targetId) {
		if (familyMapRepository.existsByGuardianIdAndKidId(
			requestMember.isGuardian() ? requestMember.getId() : targetId,
			requestMember.isKid() ? requestMember.getId() : targetId)) {
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

	private Optional<Member> getMemberByNickname(final Member requestMember, final String nickname) {
		return memberRepository.searchByNickname(nickname)
			.filter(searchedMember -> !requestMember.getNickname().equals(searchedMember.getNickname()));
	}

	private Member findMemberByMemberId(final long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다"));
	}

	private Member findMemberByMemberIdWithMemberType(final long memberId) {
		return memberRepository.findByIdWithMemberTypeDetail(memberId)
			.orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다"));
	}
}
