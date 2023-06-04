package com.polzzak.domain.stampboard.entity;

import com.polzzak.domain.model.BaseModifiableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "mission")
public class Mission extends BaseModifiableEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stamp_board_id")
	private StampBoard stampBoard;

	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	private boolean isActive;

	public void changeActivate(boolean isActive) {
		this.isActive = isActive;
	}

	@Builder(builderMethodName = "createMission")
	public Mission(StampBoard stampBoard, String content) {
		this.stampBoard = stampBoard;
		this.content = content;
		this.isActive = true;
	}
}
