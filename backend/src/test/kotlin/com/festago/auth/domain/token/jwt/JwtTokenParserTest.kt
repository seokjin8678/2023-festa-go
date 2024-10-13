package com.festago.auth.domain.token.jwt

import com.festago.auth.domain.Role
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.UnauthorizedException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainOnly
import io.kotest.matchers.throwable.shouldHaveMessage
import java.nio.charset.StandardCharsets
import java.time.Clock
import java.util.Date
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JwtTokenParserTest {
    val key = "1231231231231231223131231231231231231212312312"
    val secretKey = Keys.hmacShaKeyFor(key.toByteArray())

    lateinit var jwtTokenParser: JwtTokenParser

    @BeforeEach
    fun setUp() {
        jwtTokenParser = JwtTokenParser(
            key,
            Clock.systemDefaultZone()
        )
    }

    @Test
    fun JWT_토큰의_형식이_아니면_예외() {
        // given
        val token = "Hello World"

        // when
        val ex = shouldThrow<UnauthorizedException> {
            jwtTokenParser.getClaims(token)
        }

        // then
        ex shouldHaveMessage ErrorCode.INVALID_AUTH_TOKEN.message
    }

    @Test
    fun 기간이_만료된_토큰이면_예외() {
        // given
        val token = Jwts.builder()
            .audience().add(Role.MEMBER.name).and()
            .expiration(Date(Date().time - 1000))
            .signWith(secretKey)
            .compact()

        // when
        val ex = shouldThrow<UnauthorizedException> {
            jwtTokenParser.getClaims(token)
        }

        // then
        ex shouldHaveMessage ErrorCode.EXPIRED_AUTH_TOKEN.message
    }

    @Test
    fun 키값이_유효하지_않으면_예외() {
        // given
        val otherKey = Keys.hmacShaKeyFor(("other$secretKey").toByteArray(StandardCharsets.UTF_8))
        val token = Jwts.builder()
            .audience().add(Role.MEMBER.name).and()
            .expiration(Date(Date().time + 10000))
            .signWith(otherKey)
            .compact()

        // when
        val ex = shouldThrow<UnauthorizedException> {
            jwtTokenParser.getClaims(token)
        }

        // then
        ex shouldHaveMessage ErrorCode.INVALID_AUTH_TOKEN.message
    }

    @Test
    fun 토큰_추출_성공() {
        // given
        val token = Jwts.builder()
            .audience().add(Role.MEMBER.name).and()
            .expiration(Date(Date().time + 10000))
            .signWith(secretKey)
            .compact()

        // when
        val actual = jwtTokenParser.getClaims(token)

        // then
        actual.audience shouldContainOnly listOf(Role.MEMBER.name)
    }
}
