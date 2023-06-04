package com.polzzak.domain.stampboard.entity;

import com.polzzak.domain.model.BaseEntity;
import com.polzzak.domain.user.entity.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
public class MissionRequest extends BaseEntity {

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

	@Builder(builderMethodName = "createMissionRequest")
	public MissionRequest(StampBoard stampBoard, Mission mission, Member guardian, Member kid) {
		this.stampBoard = stampBoard;
		this.mission = mission;
		this.guardian = guardian;
		this.kid = kid;
	}
}
