package com.polzzak.security;

import com.polzzak.application.UserAuthenticationService;
import com.polzzak.application.UserService;
import com.polzzak.presentation.api.UserRestController;
import com.polzzak.support.test.ControllerTestHelper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;

import static com.polzzak.support.TokenFixtures.*;
import static com.polzzak.support.constant.Headers.REFRESH_TOKEN_HEADER;
import static org.springframework.restdocs.cookies.CookieDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserRestController.class)
public class TokenAuthenticationTest extends ControllerTestHelper {

    @MockBean
    UserService userService;

    @MockBean
    UserAuthenticationService userAuthenticationService;

    @Test
    void 엑세스_토큰_유효_하지_않음() throws Exception {
        // given
        String invalidAccessToken = INVALID_ACCESS_TOKEN;

        // when & then
        mockMvc.perform(
                get("/api/v1/users/me")
                    .header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + invalidAccessToken)
            )
            .andExpectAll(status().isUnauthorized())
            .andDo(
                document(
                    "{class-name}/access-token-invalid",
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("유효하지 않은 엑세스 토큰")
                    ),
                    responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("token").description("응답 데이터")
                    )
                )
            );
    }

    @Test
    void 엑세스_토큰_만료_리프레시_토큰_유효() throws Exception {
        // given
        String expiredAccessToken = EXPIRED_ACCESS_TOKEN;
        Cookie refreshCookie = REFRESH_COOKIE;

        // when & then
        mockMvc.perform(
                get("/api/v1/users/me")
                    .header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + expiredAccessToken)
                    .cookie(refreshCookie)
            )
            .andExpectAll(status().isBadRequest(), cookie().httpOnly(REFRESH_TOKEN_HEADER, true))
            .andDo(
                document(
                    "{class-name}/access-token-expired-refresh-token-valid",
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("만료된 엑세스 토큰")
                    ),
                    requestCookies(
                        cookieWithName(REFRESH_TOKEN_HEADER).description("유효한 리프레시 토큰")
                    ),
                    responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("token").description("새로 발급한 엑세스 토큰")
                    ),
                    responseCookies(
                        cookieWithName(REFRESH_TOKEN_HEADER).description("새로 발급한 리프레시 토큰")
                    )
                )
            );
    }

    @Test
    void 엑세스_토큰_만료_리프레시_토큰_유효_하지_않음() throws Exception {
        // given
        String expiredAccessToken = EXPIRED_ACCESS_TOKEN;
        Cookie invalidRefreshCookie = INVALID_REFRESH_COOKIE;

        // when & then
        mockMvc.perform(
                get("/api/v1/users/me")
                    .header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + expiredAccessToken)
                    .cookie(invalidRefreshCookie)
            )
            .andExpectAll(status().isUnauthorized())
            .andDo(
                document(
                    "{class-name}/access-token-expired-refresh-token-invalid",
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("만료된 엑세스 토큰")
                    ),
                    requestCookies(
                        cookieWithName(REFRESH_TOKEN_HEADER).description("유효하지 않은 리프레시 토큰")
                    ),
                    responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("token").description("응답 데이터")
                    )
                )
            );
    }
}
