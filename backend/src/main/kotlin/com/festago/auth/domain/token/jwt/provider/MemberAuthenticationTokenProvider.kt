package com.festago.auth.domain.token.jwt.provider

import com.festago.auth.domain.Role
import com.festago.auth.domain.authentication.MemberAuthentication
import com.festago.auth.dto.TokenResponse
import java.time.temporal.ChronoUnit
import org.springframework.stereotype.Component

@Component
class MemberAuthenticationTokenProvider(
    private val tokenProviderTemplate: TokenProviderTemplate,
) {

    fun provide(memberAuthentication: MemberAuthentication): TokenResponse {
        return tokenProviderTemplate.provide(EXPIRATION_HOURS, ChronoUnit.HOURS) {
            subject(memberAuthentication.memberId.toString())
            audience().add(Role.MEMBER.name)
        }
    }

    companion object {
        private const val EXPIRATION_HOURS = 6L
    }
}
