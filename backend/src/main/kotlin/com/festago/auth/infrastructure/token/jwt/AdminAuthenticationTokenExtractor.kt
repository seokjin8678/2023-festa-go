package com.festago.auth.infrastructure.token.jwt

import com.festago.auth.domain.authentication.Authentication
import com.festago.auth.domain.token.jwt.AuthenticationTokenExtractor
import com.festago.auth.domain.token.jwt.JwtTokenParser
import com.festago.auth.infrastructure.token.jwt.claims.AdminAuthenticationClaimsExtractor
import org.springframework.stereotype.Component

@Component
internal class AdminAuthenticationTokenExtractor(
    private val jwtTokenParser: JwtTokenParser,
    private val adminAuthenticationClaimsExtractor: AdminAuthenticationClaimsExtractor,
) : AuthenticationTokenExtractor {

    override fun extract(token: String): Authentication {
        val claims = jwtTokenParser.getClaims(token)
        return adminAuthenticationClaimsExtractor.extract(claims)
    }
}
