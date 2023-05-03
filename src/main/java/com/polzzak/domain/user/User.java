package com.polzzak.domain.user;

import com.polzzak.support.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users", indexes = @Index(name = "idx_username", columnList = "username"))
public class User extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Column(nullable = false)
    private boolean withdraw;

    @Column(nullable = false)
    private LocalDateTime signedDate;

    @JoinColumn(name = "member_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    private Member member;

    @Builder(builderMethodName = "createUser")
    public User(final String username, final SocialType socialType, final Member member) {
        this.username = username;
        this.socialType = socialType;
        this.member = member;
        this.withdraw = false;
        this.signedDate = LocalDateTime.now();
    }

    public void withdraw() {
        this.withdraw = true;
    }
}
