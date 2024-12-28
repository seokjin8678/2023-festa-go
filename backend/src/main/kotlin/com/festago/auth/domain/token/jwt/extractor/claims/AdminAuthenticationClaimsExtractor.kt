package com.festago.auth.domain.token.jwt.extractor.claims

import com.festago.auth.domain.Role
import com.festago.auth.domain.authentication.AdminAuthentication
import com.festago.auth.domain.authentication.AnonymousAuthentication
import com.festago.auth.domain.authentication.Authentication
import io.jsonwebtoken.Claims
import org.springframework.stereotype.Component

@Component
internal class AdminAuthenticationClaimsExtractor : AuthenticationClaimsExtractor {

    override fun extract(claims: Claims): Authentication {
        if (!claims.audience.contains(Role.ADMIN.name)) {
            return AnonymousAuthentication
        }
        val memberId = claims.subject.toLong()
        return AdminAuthentication(memberId)
    }
}
