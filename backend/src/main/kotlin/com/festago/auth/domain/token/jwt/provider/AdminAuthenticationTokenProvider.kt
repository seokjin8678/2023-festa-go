package com.festago.auth.domain.token.jwt.provider

import com.festago.auth.domain.Role
import com.festago.auth.domain.authentication.AdminAuthentication
import com.festago.auth.dto.TokenResponse
import java.time.temporal.ChronoUnit
import org.springframework.stereotype.Component

@Component
class AdminAuthenticationTokenProvider(
    private val tokenProviderTemplate: TokenProviderTemplate,
) {

    fun provide(adminAuthentication: AdminAuthentication): TokenResponse {
        return tokenProviderTemplate.provide(EXPIRATION_HOURS, ChronoUnit.HOURS) {
            subject(adminAuthentication.memberId.toString())
            audience().add(Role.ADMIN.name)
        }
    }

    companion object {
        private const val EXPIRATION_HOURS = 24L
    }
}
