package com.festago.auth.domain.token.jwt

import com.festago.common.exception.ErrorCode
import com.festago.common.exception.UnauthorizedException
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.JwtParser

private val log = KotlinLogging.logger {}

class OpenIdIdTokenParser(
    private val jwtParser: JwtParser,
) {

    fun parse(idToken: String): Claims {
        try {
            return jwtParser.parseSignedClaims(idToken).payload
        } catch (e: JwtException) {
            log.info { "OpenID Token 파싱에서 예외가 발생했습니다. message=${e.message}" }
            throw UnauthorizedException(ErrorCode.OPEN_ID_INVALID_TOKEN)
        } catch (e: IllegalArgumentException) {
            log.info { "OpenID Token 파싱에서 예외가 발생했습니다. message=${e.message}" }
            throw UnauthorizedException(ErrorCode.OPEN_ID_INVALID_TOKEN)
        } catch (e: Exception) {
            log.error(e) { "JWT 토큰 파싱 중에 문제가 발생했습니다." }
            throw e
        }
    }
}