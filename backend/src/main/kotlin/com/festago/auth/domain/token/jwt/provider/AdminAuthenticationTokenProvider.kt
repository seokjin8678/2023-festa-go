package com.festago.auth.domain.token.jwt.provider

import com.festago.auth.domain.Role
import com.festago.auth.domain.authentication.AdminAuthentication
import com.festago.auth.dto.TokenResponse
import org.springframework.stereotype.Component

@Component
class AdminAuthenticationTokenProvider(
    private val tokenProviderTemplate: TokenProviderTemplate,
) {

    fun provide(adminAuthentication: AdminAuthentication): TokenResponse {
        return tokenProviderTemplate.provide(EXPIRATION_MINUTES) { jwtBuilder ->
            jwtBuilder
                .subject(adminAuthentication.memberId.toString())
                .audience().add(Role.ADMIN.name).and()
        }
    }

    companion object {
        private const val EXPIRATION_MINUTES = 60L * 24L
    }
}
