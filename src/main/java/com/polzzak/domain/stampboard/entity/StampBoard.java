package com.polzzak.domain.stampboard.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.hibernate.annotations.Where;

import com.polzzak.domain.model.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "stamp_board")
public class StampBoard extends BaseEntity {

	@Column(nullable = false)
	private long guardianId;

	@Column(nullable = false)
	private long kidId;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status;

	@Column(nullable = false)
	private int currentStampCount;

	@Column(nullable = false)
	private int goalStampCount;

	@Column(nullable = false)
	private String reward;

	private LocalDateTime completedDate;

	private LocalDateTime rewardDate;

	@Where(clause = "is_active = true")
	@OneToMany(mappedBy = "stampBoard", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<Mission> missions = new ArrayList<>();
	@OneToMany(mappedBy = "stampBoard", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<Stamp> stamps = new ArrayList<>();
	@OneToMany(mappedBy = "stampBoard", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<MissionComplete> missionCompletes = new ArrayList<>();

	@Builder(builderMethodName = "createStampBoard")
	public StampBoard(long guardianId, long kidId, String name, int goalStampCount, String reward) {
		this.guardianId = guardianId;
		this.kidId = kidId;
		this.name = name;
		this.status = Status.PROGRESS;
		this.currentStampCount = 0;
		this.goalStampCount = goalStampCount;
		this.reward = reward;
	}

	public boolean isValidStamp(long stampId) {
		return stamps.stream()
			.anyMatch(stamp -> stamp.getId() == stampId);
	}

	public boolean isNotOwner(long memberId) {
		return kidId != memberId && guardianId != memberId;
	}

	public void updateName(String name) {
		this.name = name;
	}

	public void addStampCount(int stampCount) {
		this.currentStampCount += stampCount;

		if (isCompleted()) {
			complete();
		}
	}

	public boolean isCompleted() {
		return currentStampCount == goalStampCount;
	}

	public void updateReward(String reward) {
		this.reward = reward;
	}

	private void complete() {
		status = StampBoard.Status.COMPLETED;
		completedDate = LocalDateTime.now();
	}

	@RequiredArgsConstructor
	public enum Status {
		//진행
		PROGRESS("도장 모으는 중"), COMPLETED("도장 다 모음"), ISSUED_COUPON("쿠폰 발급"),
		//종료
		REWARDED("쿠폰 수령");

		private final String description;

		public static List<Status> getProgressStatuses() {
			return List.of(PROGRESS, COMPLETED, ISSUED_COUPON);
		}

		public static String getLowerCase(Status status) {
			return String.valueOf(status.name()).toLowerCase(Locale.ROOT);
		}
	}
}
