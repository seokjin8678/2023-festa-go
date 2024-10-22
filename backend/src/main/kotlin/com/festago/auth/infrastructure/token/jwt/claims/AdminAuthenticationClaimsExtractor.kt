package com.festago.auth.infrastructure.token.jwt.claims

import com.festago.auth.domain.Role
import com.festago.auth.domain.authentication.AdminAuthentication
import com.festago.auth.domain.authentication.AnonymousAuthentication
import com.festago.auth.domain.authentication.Authentication
import com.festago.auth.domain.token.jwt.claims.AuthenticationClaimsExtractor
import io.jsonwebtoken.Claims
import org.springframework.stereotype.Component

@Component
internal class AdminAuthenticationClaimsExtractor : AuthenticationClaimsExtractor {

    override fun extract(claims: Claims): Authentication {
        if (!claims.audience.contains(Role.ADMIN.name)) {
            return AnonymousAuthentication
        }
        val adminId = claims[ADMIN_ID_KEY] as Long
        return AdminAuthentication(adminId)
    }

    companion object {
        private const val ADMIN_ID_KEY = "adminId"
    }
}
