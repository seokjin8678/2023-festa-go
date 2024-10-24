package com.festago.auth.domain.token.jwt.extractor

import com.festago.auth.domain.authentication.Authentication
import com.festago.auth.domain.token.jwt.AuthenticationTokenExtractor
import com.festago.auth.domain.token.jwt.JwtTokenParser
import com.festago.auth.domain.token.jwt.extractor.claims.MemberAuthenticationClaimsExtractor
import org.springframework.stereotype.Component

@Component
internal class MemberAuthenticationTokenExtractor(
    private val jwtTokenParser: JwtTokenParser,
    private val memberAuthenticationClaimsExtractor: MemberAuthenticationClaimsExtractor,
) : AuthenticationTokenExtractor {

    override fun extract(token: String): Authentication {
        val claims = jwtTokenParser.getClaims(token)
        return memberAuthenticationClaimsExtractor.extract(claims)
    }
}
