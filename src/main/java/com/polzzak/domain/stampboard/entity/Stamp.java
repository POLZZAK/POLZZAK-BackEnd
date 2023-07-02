package com.polzzak.domain.stampboard.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.polzzak.domain.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "stamp")
public class Stamp extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stamp_board_id")
	private StampBoard stampBoard;

	@Column(nullable = false)
	private int stampDesignId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mission_id")
	private Mission mission;

	@Builder(builderMethodName = "createStamp")
	public Stamp(StampBoard stampBoard, Mission mission, int stampDesignId) {
		this.stampBoard = stampBoard;
		this.mission = mission;
		this.stampDesignId = stampDesignId;
	}
}
