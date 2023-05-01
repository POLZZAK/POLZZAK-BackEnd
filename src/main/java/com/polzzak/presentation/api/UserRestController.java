package com.polzzak.presentation.api;

import com.polzzak.application.UserAuthenticationService;
import com.polzzak.application.UserService;
import com.polzzak.application.dto.*;
import com.polzzak.domain.user.SocialType;
import com.polzzak.security.LoginUsername;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.polzzak.support.constant.Headers.REFRESH_TOKEN_HEADER;

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
    public ResponseEntity<ApiResponse> loginKakao(
        final @RequestBody @Valid LoginRequest loginRequest,
        final HttpServletResponse httpServletResponse
    ) {
        String username = userAuthenticationService.getKakaoUserInfo(loginRequest);
        return login(username, SocialType.KAKAO, httpServletResponse);
    }

    @PostMapping("/login/google")
    public ResponseEntity<ApiResponse> loginGoogle(
        final @RequestBody @Valid LoginRequest loginRequest,
        final HttpServletResponse httpServletResponse
    ) {
        String username = userAuthenticationService.getGoogleUserInfo(loginRequest);
        return login(username, SocialType.GOOGLE, httpServletResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(
        final @RequestPart @Valid RegisterRequest registerRequest,
        final @RequestPart(required = false) MultipartFile profile,
        final HttpServletResponse httpServletResponse
    ) {
        UserDto userDto = userAuthenticationService.register(registerRequest, profile);
        return getTokenResponseEntity(httpServletResponse, userDto.username());
    }

    @GetMapping("/validate/nickname")
    public ResponseEntity validateNickname(final @RequestParam("value") String nickname) {
        userAuthenticationService.validNickname(nickname);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getUserInfo(final @LoginUsername String username) {
        return ResponseEntity.ok(ApiResponse.ok(UserResponse.from(userService.getUserInfo(username))));
    }

    private ResponseEntity<ApiResponse> login(final String username, final SocialType socialType, final HttpServletResponse response) {
        try {
            UserDto findUser = userService.getUserInfo(username);
            return getTokenResponseEntity(response, findUser.username());
        } catch (IllegalArgumentException e) {
            LoginResponse loginResponse = new LoginResponse(username, socialType);
            return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.REQUIRED_REGISTER, loginResponse));
        }
    }

    private ResponseEntity<ApiResponse> getTokenResponseEntity(final HttpServletResponse response, final String username) {
        addRefreshCookie(response, username);
        TokenResponse tokenResponse = new TokenResponse(userAuthenticationService.generateAccessTokenByUsername(username));
        return ResponseEntity.ok(ApiResponse.ok(tokenResponse));
    }

    private void addRefreshCookie(final HttpServletResponse response, final String payload) {
        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_HEADER, userAuthenticationService.generateRefreshTokenByUsername(payload));
        refreshTokenCookie.setHttpOnly(true);
        response.addCookie(refreshTokenCookie);
    }
}
