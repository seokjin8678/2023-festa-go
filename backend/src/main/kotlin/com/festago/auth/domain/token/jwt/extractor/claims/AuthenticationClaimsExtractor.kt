package com.festago.auth.domain.token.jwt.extractor.claims

import com.festago.auth.domain.authentication.Authentication
import io.jsonwebtoken.Claims

/**
 * AuthenticationTokenExtractor의 필드로 사용되기 위해 설계되었음
 *
 * JWT Claims에서 값을 추출하여 Authentication을 반환하는 인터페이스
 *
 * 구현체에서 반환하는 Authentication는 반드시 null이 아니여야 한다.
 *
 * null을 반환하는 대신 AnonymousAuthentication.getInstance() 반환할 것!
 */
fun interface AuthenticationClaimsExtractor {
    fun extract(claims: Claims): Authentication
}
