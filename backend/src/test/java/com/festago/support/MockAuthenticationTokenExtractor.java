package com.festago.support;

import com.festago.auth.domain.authentication.AuthenticateContext;
import com.festago.auth.domain.authentication.Authentication;
import com.festago.auth.domain.token.jwt.AuthenticationTokenExtractor;

public class MockAuthenticationTokenExtractor implements AuthenticationTokenExtractor {

    private final AuthenticateContext authenticateContext;

    public MockAuthenticationTokenExtractor(AuthenticateContext authenticateContext) {
        this.authenticateContext = authenticateContext;
    }

    @Override
    public Authentication extract(String token) {
        return authenticateContext.getAuthentication();
    }
}
