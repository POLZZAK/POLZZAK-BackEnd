package com.polzzak.user;

import com.polzzak.auth.UserAuthenticationService;
import com.polzzak.user.model.LoginRequest;
import com.polzzak.common.model.ApiResponse;
import com.polzzak.common.model.ResultCode;
import com.polzzak.user.model.LoginUsername;
import com.polzzak.user.model.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.polzzak.auth.model.Headers.REFRESH_TOKEN_HEADER;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    private final UserService userService;
    private final UserAuthenticationService userAuthenticationService;

    public UserRestController(final UserService userService, final UserAuthenticationService userAuthenticationService) {
        this.userService = userService;
        this.userAuthenticationService = userAuthenticationService;
    }

    @PostMapping("/login/kakao")
    public ResponseEntity<ApiResponse<AccessTokenResponse>> loginKakao(
        final @RequestBody @Valid LoginRequest loginRequest,
        final HttpServletResponse httpServletResponse
    ) {
        String username = userAuthenticationService.getKakaoUserInfo(loginRequest);
        userService.validateUser(username, SocialType.KAKAO);
        httpServletResponse.addCookie(userAuthenticationService.addRefreshCookie(username));
        return ResponseEntity.ok(ApiResponse.ok(userAuthenticationService.getAccessTokenResponse(username)));
    }

    @PostMapping("/login/google")
    public ResponseEntity<ApiResponse<AccessTokenResponse>> loginGoogle(
        final @RequestBody @Valid LoginRequest loginRequest,
        final HttpServletResponse httpServletResponse
    ) {
        String username = userAuthenticationService.getKakaoUserInfo(loginRequest);
        userService.validateUser(username, SocialType.GOOGLE);
        httpServletResponse.addCookie(userAuthenticationService.addRefreshCookie(username));
        return ResponseEntity.ok(ApiResponse.ok(userAuthenticationService.getAccessTokenResponse(username)));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(
        final @RequestPart @Valid RegisterRequest registerRequest,
        final @RequestPart(required = false) MultipartFile profile,
        final HttpServletResponse httpServletResponse
    ) {
        UserDto userDto = userAuthenticationService.register(registerRequest, profile);
        String username = userDto.username();
        httpServletResponse.addCookie(userAuthenticationService.addRefreshCookie(username));
        return ResponseEntity.ok(ApiResponse.ok(userAuthenticationService.getAccessTokenResponse(username)));
    }

    @GetMapping("/validate/nickname")
    public ResponseEntity validateNickname(final @RequestParam("value") String nickname) {
        userAuthenticationService.validateNickname(nickname);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getUserInfo(final @LoginUsername String username) {
        return ResponseEntity.ok(ApiResponse.ok(UserResponse.from(userService.getUserInfo(username))));
    }

}
