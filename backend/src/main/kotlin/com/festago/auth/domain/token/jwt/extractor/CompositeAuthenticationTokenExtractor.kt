package com.festago.auth.domain.token.jwt.extractor

import com.festago.auth.domain.Role
import com.festago.auth.domain.authentication.AnonymousAuthentication
import com.festago.auth.domain.authentication.Authentication
import com.festago.auth.domain.token.jwt.AuthenticationTokenExtractor
import com.festago.auth.domain.token.jwt.JwtTokenParser
import com.festago.auth.domain.token.jwt.extractor.claims.AuthenticationClaimsExtractor
import org.springframework.stereotype.Component

@Component
internal class CompositeAuthenticationTokenExtractor(
    private val jwtTokenParser: JwtTokenParser,
    private val authenticationClaimsExtractors: List<AuthenticationClaimsExtractor>,
) : AuthenticationTokenExtractor {

    override fun extract(token: String): Authentication {
        val claims = jwtTokenParser.getClaims(token)
        for (claimsExtractor in authenticationClaimsExtractors) {
            val authentication = claimsExtractor.extract(claims)
            if (authentication.role != Role.ANONYMOUS) {
                return authentication
            }
        }
        return AnonymousAuthentication
    }
}
