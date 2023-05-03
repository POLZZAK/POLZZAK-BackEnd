package com.polzzak.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FamilyMap extends BaseEntity {
    @Column(nullable = false)
    private Long guardianId;

    @Column(nullable = false)
    private Long kidId;

    @Builder(builderMethodName = "createFamilyMap")
    public FamilyMap(final Long guardianId, final Long kidId) {
        this.guardianId = guardianId;
        this.kidId = kidId;
    }
}
