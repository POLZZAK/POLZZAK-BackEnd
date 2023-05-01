package com.polzzak.application;

import com.polzzak.application.dto.UserDto;
import com.polzzak.domain.user.User;
import com.polzzak.domain.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final FileService fileService;


    public UserService(final UserRepository userRepository, final FileService fileService) {
        this.userRepository = userRepository;
        this.fileService = fileService;
    }

    public UserDto getUserInfo(final String username) {
        User findUser = findByUsername(username);
        return UserDto.from(findUser, fileService.getSignedUrl(findUser.getMember().getProfileKey()));
    }

    private User findByUsername(final String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다"));
    }
}