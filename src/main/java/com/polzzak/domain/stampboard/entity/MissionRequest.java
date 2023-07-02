package com.polzzak.domain.stampboard.entity;

import com.polzzak.domain.model.BaseEntity;
import com.polzzak.domain.user.entity.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "mission_complete")
//TODO jjh table 이름 수정
public class MissionRequest extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stamp_board_id")
	private StampBoard stampBoard;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mission_id")
	private Mission mission;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "guardian_id")
	private Member guardian;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kid_id")
	private Member kid;

	@Builder(builderMethodName = "createMissionRequest")
	public MissionRequest(StampBoard stampBoard, Mission mission, Member guardian, Member kid) {
		this.stampBoard = stampBoard;
		this.mission = mission;
		this.guardian = guardian;
		this.kid = kid;
	}
	public boolean isNotOwner(long memberId) {
		return kid.getId() != memberId && guardian.getId() != memberId;
	}
}
