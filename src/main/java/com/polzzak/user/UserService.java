package com.polzzak.user;

import com.polzzak.common.model.PolzzakException;
import com.polzzak.common.model.ResultCode;
import com.polzzak.file.FileService;
import com.polzzak.user.model.LoginResponse;
import com.polzzak.user.model.SocialType;
import com.polzzak.user.model.UserDto;
import com.polzzak.user.model.User;
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

    public void validateUser(final String username, final SocialType socialType) {
        try {
            User findUser = findByUsername(username);
        } catch (Exception e) {
            LoginResponse loginResponse = new LoginResponse(username, socialType);
            throw new PolzzakException(ResultCode.REQUIRED_REGISTER);
        }
    }

    private User findByUsername(final String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다"));
    }
}
