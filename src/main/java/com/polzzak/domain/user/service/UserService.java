package com.polzzak.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polzzak.domain.user.dto.UserDto;
import com.polzzak.domain.user.entity.User;
import com.polzzak.domain.user.repository.UserRepository;
import com.polzzak.global.infra.file.FileClient;

@Service
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;
	private final FileClient fileClient;

	public UserService(final UserRepository userRepository, final FileClient fileClient) {
		this.userRepository = userRepository;
		this.fileClient = fileClient;
	}

	public UserDto getUserInfo(final String username) {
		User findUser = findByUsername(username);
		return UserDto.from(findUser, findUser.getSocialType(),
			fileClient.getSignedUrl(findUser.getMember().getProfileKey()));
	}

	private User findByUsername(final String username) {
		return userRepository.findByUsername(username)
			.orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다"));
	}
}
