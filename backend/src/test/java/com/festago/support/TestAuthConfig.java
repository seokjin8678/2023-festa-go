package com.festago.support;

import com.festago.auth.domain.authentication.AuthenticateContext;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestAuthConfig {

    @Bean
    public MockAuthenticationTokenExtractor mockAuthenticationTokenExtractor() {
        return new MockAuthenticationTokenExtractor(authenticateContext());
    }

    @Bean
    public AuthenticateContext authenticateContext() {
        return new AuthenticateContext();
    }
}
