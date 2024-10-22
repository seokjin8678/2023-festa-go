package com.festago.auth.domain.token.jwt.provider

import com.festago.auth.dto.TokenResponse
import io.jsonwebtoken.JwtBuilder
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.nio.charset.StandardCharsets
import java.time.Clock
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.function.UnaryOperator
import javax.crypto.SecretKey
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class TokenProviderTemplate(
    @Value("\${festago.auth-secret-key}") secretKey: String,
    private val clock: Clock,
) {
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(secretKey.toByteArray(StandardCharsets.UTF_8))

    fun provide(expirationMinutes: Long, template: UnaryOperator<JwtBuilder>): TokenResponse {
        val now = clock.instant()
        val expiredAt = now.plus(expirationMinutes, ChronoUnit.MINUTES)
        val builder = Jwts.builder()
            .expiration(Date.from(expiredAt))
            .issuedAt(Date.from(now))
            .signWith(secretKey)
        template.apply(builder)
        val accessToken = builder.compact()
        return TokenResponse(
            accessToken,
            LocalDateTime.ofInstant(expiredAt, clock.zone)
        )
    }
}
