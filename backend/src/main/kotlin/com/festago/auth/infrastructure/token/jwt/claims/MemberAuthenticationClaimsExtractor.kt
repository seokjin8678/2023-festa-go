package com.festago.auth.infrastructure.token.jwt.claims

import com.festago.auth.domain.Role
import com.festago.auth.domain.authentication.AnonymousAuthentication
import com.festago.auth.domain.authentication.Authentication
import com.festago.auth.domain.authentication.MemberAuthentication
import com.festago.auth.domain.token.jwt.claims.AuthenticationClaimsExtractor
import io.jsonwebtoken.Claims
import org.springframework.stereotype.Component

@Component
internal class MemberAuthenticationClaimsExtractor : AuthenticationClaimsExtractor {

    override fun extract(claims: Claims): Authentication {
        if (!claims.audience.contains(Role.MEMBER.name)) {
            return AnonymousAuthentication
        }
        val memberId = claims[MEMBER_ID_KEY] as Long
        return MemberAuthentication(memberId)
    }

    companion object {
        private const val MEMBER_ID_KEY = "memberId"
    }
}