package com.polzzak.domain.stampboard.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.polzzak.domain.user.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "mission_complete")
@EntityListeners(AuditingEntityListener.class)
public class MissionRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stamp_board_id")
	private StampBoard stampBoard;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mission_id")
	private Mission mission;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "guardian_id")
	private Member guardian;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kid_id")
	private Member kid;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdDate;

	@Builder(builderMethodName = "createMissionRequest")
	public MissionRequest(StampBoard stampBoard, Mission mission, Member guardian, Member kid) {
		this.stampBoard = stampBoard;
		this.mission = mission;
		this.guardian = guardian;
		this.kid = kid;
	}
}
