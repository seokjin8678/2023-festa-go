package com.festago.auth.domain.token.jwt.provider

import com.festago.auth.domain.Role
import com.festago.auth.domain.authentication.MemberAuthentication
import com.festago.auth.dto.TokenResponse
import org.springframework.stereotype.Component

@Component
class MemberAuthenticationTokenProvider(
    private val tokenProviderTemplate: TokenProviderTemplate,
) {

    fun provide(memberAuthentication: MemberAuthentication): TokenResponse {
        return tokenProviderTemplate.provide(EXPIRATION_MINUTES) { jwtBuilder ->
            jwtBuilder
                .subject(memberAuthentication.memberId.toString())
                .audience().add(Role.MEMBER.name).and()
        }
    }

    companion object {
        private const val EXPIRATION_MINUTES = 60L * 6L
    }
}
