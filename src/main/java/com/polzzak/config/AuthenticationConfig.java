package com.polzzak.config;

import com.polzzak.security.LoginUsernameResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class AuthenticationConfig implements WebMvcConfigurer {
    private final LoginUsernameResolver loginUsernameResolver;

    public AuthenticationConfig(final LoginUsernameResolver loginUsernameResolver) {
        this.loginUsernameResolver = loginUsernameResolver;
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginUsernameResolver);
    }
}
