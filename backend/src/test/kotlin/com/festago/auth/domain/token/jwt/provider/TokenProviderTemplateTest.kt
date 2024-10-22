package com.festago.auth.domain.token.jwt.provider

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.kotest.matchers.shouldBe
import java.time.Clock
import org.junit.jupiter.api.Test

class TokenProviderTemplateTest {
    val key = "1231231231231231223131231231231231231212312312"
    val tokenProviderTemplate = TokenProviderTemplate(
        key,
        Clock.systemDefaultZone()
    )

    @Test
    fun 토큰_생성_성공() {
        // given
        val parser = Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(key.toByteArray()))
            .build()

        // when
        val actual = tokenProviderTemplate.provide(60) { it }

        // then
        parser.isSigned(actual.token) shouldBe true
    }
}
