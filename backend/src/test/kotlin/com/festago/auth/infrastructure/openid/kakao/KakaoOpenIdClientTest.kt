package com.festago.auth.infrastructure.openid.kakao

import com.festago.auth.infrastructure.openid.NoopOpenIdNonceValidator
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.UnauthorizedException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import java.time.Clock
import java.time.temporal.ChronoUnit
import java.util.Date
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class KakaoOpenIdClientTest {
    lateinit var kakaoOpenIdClient: KakaoOpenIdClient
    lateinit var keyLocator: KakaoOpenIdPublicKeyLocator
    lateinit var clock: Clock
    val key = Keys.hmacShaKeyFor("key".repeat(15).toByteArray())

    @BeforeEach
    fun setUp() {
        keyLocator = mockk()
        clock = spyk(Clock.systemDefaultZone())
        kakaoOpenIdClient = KakaoOpenIdClient(
            openIdNonceValidator = NoopOpenIdNonceValidator(),
            restApiKey = "restApiKey",
            nativeAppKey = "nativeApiKey",
            kakaoOpenIdPublicKeyLocator = keyLocator,
            clock = clock
        )
    }

    @Test
    fun audience가_올바르지_않으면_예외() {
        // given
        every { keyLocator.locate(any()) } returns key
        val token = Jwts.builder()
            .audience().add("wrong")
            .and()
            .issuer("https://kauth.kakao.com")
            .signWith(key)
            .expiration(Date.from(clock.instant().plus(1, ChronoUnit.DAYS)))
            .claim("nonce", "value")
            .compact()

        // when
        val ex = shouldThrow<UnauthorizedException> {
            kakaoOpenIdClient.getUserInfo(token)
        }

        // then
        ex shouldHaveMessage ErrorCode.OPEN_ID_INVALID_TOKEN.message
    }

    @Test
    fun issuer가_올바르지_않으면_예외() {
        // given
        every { keyLocator.locate(any()) } returns key
        val token = Jwts.builder()
            .audience().add("restApiKey")
            .and()
            .issuer("wrong")
            .signWith(key)
            .expiration(Date.from(clock.instant().plus(1, ChronoUnit.DAYS)))
            .compact()

        // when
        val ex = shouldThrow<UnauthorizedException> {
            kakaoOpenIdClient.getUserInfo(token)
        }

        // then
        ex shouldHaveMessage ErrorCode.OPEN_ID_INVALID_TOKEN.message
    }

    @Test
    fun 토큰이_만료되면_예외() {
        // given
        every { keyLocator.locate(any()) } returns key
        val token = Jwts.builder()
            .audience().add("restApiKey")
            .and()
            .issuer("https://kauth.kakao.com")
            .signWith(key)
            .expiration(Date.from(clock.instant().minus(1, ChronoUnit.DAYS)))
            .compact()

        // when
        val ex = shouldThrow<UnauthorizedException> {
            kakaoOpenIdClient.getUserInfo(token)
        }

        // then
        ex shouldHaveMessage ErrorCode.OPEN_ID_INVALID_TOKEN.message
    }

    @Test
    fun 토큰에_서명된_키가_파싱할때_키와_일치하지_않으면_예외() {
        // given
        val otherKey = Keys.hmacShaKeyFor("otherKey".repeat(15).toByteArray())
        every { keyLocator.locate(any()) } returns otherKey
        val token = Jwts.builder()
            .audience().add("restApiKey")
            .and()
            .issuer("https://kauth.kakao.com")
            .signWith(key)
            .subject("12345")
            .expiration(Date.from(clock.instant().plus(1, ChronoUnit.DAYS)))
            .claim("nonce", "value")
            .compact()

        // when
        val ex = shouldThrow<UnauthorizedException> {
            kakaoOpenIdClient.getUserInfo(token)
        }

        // then
        ex shouldHaveMessage ErrorCode.OPEN_ID_INVALID_TOKEN.message
    }

    @Test
    fun audience_issuer가_올바르면_성공() {
        every { keyLocator.locate(any()) } returns key
        val token = Jwts.builder()
            .audience().add("restApiKey")
            .and()
            .issuer("https://kauth.kakao.com")
            .signWith(key)
            .subject("12345")
            .expiration(Date.from(clock.instant().plus(1, ChronoUnit.DAYS)))
            .claim("nonce", "value")
            .compact()

        // when
        val actual = kakaoOpenIdClient.getUserInfo(token)

        // then
        actual.socialId shouldBe "12345"
    }

    @ParameterizedTest
    @ValueSource(strings = ["restApiKey", "nativeApiKey"])
    fun audience_값은_restApiKey_nativeAppKey_둘_중_하나라도_매칭되면_성공(audience: String) {
        // given
        every { keyLocator.locate(any()) } returns key
        val token = Jwts.builder()
            .audience().add(audience)
            .and()
            .issuer("https://kauth.kakao.com")
            .signWith(key)
            .subject("12345")
            .expiration(Date.from(clock.instant().plus(1, ChronoUnit.DAYS)))
            .claim("nonce", "value")
            .compact()

        // when
        val actual = kakaoOpenIdClient.getUserInfo(token)

        // then
        actual.socialId shouldBe "12345"
    }
}