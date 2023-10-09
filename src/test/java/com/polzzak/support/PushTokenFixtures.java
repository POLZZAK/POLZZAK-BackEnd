package com.polzzak.support;

import com.polzzak.domain.membertype.entity.MemberType;
import com.polzzak.domain.membertype.entity.MemberTypeDetail;
import com.polzzak.domain.pushtoken.model.CreatePushToken;
import com.polzzak.domain.user.entity.Member;

public class PushTokenFixtures {

	public static final Member MEMBER = new Member("NICK", "profileKEY", new MemberTypeDetail(MemberType.KID, "kid"));
	public static final CreatePushToken CREATE_PUSH_TOKEN = new CreatePushToken("token");
}
