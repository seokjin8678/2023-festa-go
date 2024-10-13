package com.festago.auth.domain.token.jwt

import com.festago.common.exception.ErrorCode
import com.festago.common.exception.UnauthorizedException
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.nio.charset.StandardCharsets
import java.time.Clock
import java.util.Date
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
class JwtTokenParser(
    @Value("\${festago.auth-secret-key}") secretKey: String,
    clock: Clock,
) {
    private val jwtParser: JwtParser = Jwts.parser()
        .clock { Date.from(clock.instant()) }
        .verifyWith(Keys.hmacShaKeyFor(secretKey.toByteArray(StandardCharsets.UTF_8)))
        .build()

    fun getClaims(token: String): Claims {
        try {
            return jwtParser.parseSignedClaims(token).payload
        } catch (e: ExpiredJwtException) {
            throw UnauthorizedException(ErrorCode.EXPIRED_AUTH_TOKEN)
        } catch (e: JwtException) {
            throw UnauthorizedException(ErrorCode.INVALID_AUTH_TOKEN)
        } catch (e: IllegalArgumentException) {
            throw UnauthorizedException(ErrorCode.INVALID_AUTH_TOKEN)
        } catch (e: Exception) {
            log.error { "JWT 토큰 파싱 중에 문제가 발생했습니다." }
            throw e
        }
    }
}
