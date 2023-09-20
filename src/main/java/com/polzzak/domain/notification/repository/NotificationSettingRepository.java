package com.polzzak.domain.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.polzzak.domain.notification.entity.NotificationSetting;
import com.polzzak.domain.user.entity.Member;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {

	NotificationSetting findByMember(Member member);
}
