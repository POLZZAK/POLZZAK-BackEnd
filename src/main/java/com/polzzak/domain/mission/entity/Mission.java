package com.polzzak.domain.mission.entity;

import com.polzzak.domain.model.BaseEntity;
import com.polzzak.domain.stamp.entity.StampBoard;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "mission")
public class Mission extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stamp_board_id")
	private StampBoard stampBoard;

	@Column(nullable = false)
	private String content;

	@Setter
	@Column(nullable = false)
	private boolean isActive;

	@Builder(builderMethodName = "createMission")
	public Mission(StampBoard stampBoard, String content) {
		this.stampBoard = stampBoard;
		this.content = content;
		this.isActive = true;
	}
}
