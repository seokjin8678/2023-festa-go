package com.festago.auth.infrastructure.openid.apple

import com.festago.auth.infrastructure.openid.NoopOpenIdNonceValidator
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.UnauthorizedException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import java.time.Clock
import java.time.temporal.ChronoUnit
import java.util.Date
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class AppleOpenIdClientTest {

    lateinit var appleOpenIdClient: AppleOpenIdClient

    lateinit var keyLocator: AppleOpenIdPublicKeyLocator

    lateinit var clock: Clock

    val key = Keys.hmacShaKeyFor("key".repeat(15).toByteArray())

    @BeforeEach
    fun setUp() {
        keyLocator = mockk()
        clock = spyk(Clock.systemDefaultZone())
        appleOpenIdClient = AppleOpenIdClient(
            openIdNonceValidator = NoopOpenIdNonceValidator(),
            clientId = "appleClientId",
            appleOpenIdPublicKeyLocator = keyLocator,
            clock = clock
        )
    }

    @Test
    fun audience가_올바르지_않으면_예외() {
        // given
        every { keyLocator.locate(any()) } returns key
        val idToken = Jwts.builder()
            .audience().add("wrong")
            .and()
            .issuer("https://appleid.apple.com")
            .signWith(key)
            .expiration(Date.from(clock.instant().plus(1, ChronoUnit.DAYS)))
            .compact()

        // when & then
        Assertions.assertThatThrownBy { appleOpenIdClient.getUserInfo(idToken) }
            .isInstanceOf(UnauthorizedException::class.java)
            .hasMessage(ErrorCode.OPEN_ID_INVALID_TOKEN.message)
    }

    @Test
    fun issuer가_올바르지_않으면_예외() {
        // given
        every { keyLocator.locate(any()) } returns key
        val idToken = Jwts.builder()
            .audience().add("client-id")
            .and()
            .issuer("wrong")
            .signWith(key)
            .expiration(Date.from(clock.instant().plus(1, ChronoUnit.DAYS)))
            .compact()

        // when & then
        Assertions.assertThatThrownBy { appleOpenIdClient.getUserInfo(idToken) }
            .isInstanceOf(UnauthorizedException::class.java)
            .hasMessage(ErrorCode.OPEN_ID_INVALID_TOKEN.message)
    }

    @Test
    fun 토큰이_만료되면_예외() {
        // given
        every { keyLocator.locate(any()) } returns key
        val yesterday = Date.from(clock.instant().minus(1, ChronoUnit.DAYS))
        val idToken = Jwts.builder()
            .audience().add("client-id")
            .and()
            .issuer("https://appleid.apple.com")
            .signWith(key)
            .expiration(yesterday)
            .compact()

        // when & then
        Assertions.assertThatThrownBy { appleOpenIdClient.getUserInfo(idToken) }
            .isInstanceOf(UnauthorizedException::class.java)
            .hasMessage(ErrorCode.OPEN_ID_INVALID_TOKEN.message)
    }

    @Test
    fun 토큰에_서명된_키가_파싱할때_키와_일치하지_않으면_예외() {
        // given
        val otherKey = Keys.hmacShaKeyFor("otherKey".repeat(10).toByteArray())
        every { keyLocator.locate(any()) } returns otherKey
        val idToken = Jwts.builder()
            .audience().add("client-id")
            .and()
            .issuer("https://appleid.apple.com")
            .signWith(key)
            .expiration(Date.from(clock.instant().plus(1, ChronoUnit.DAYS)))
            .compact()

        // when & then
        Assertions.assertThatThrownBy { appleOpenIdClient.getUserInfo(idToken) }
            .isInstanceOf(UnauthorizedException::class.java)
            .hasMessage(ErrorCode.OPEN_ID_INVALID_TOKEN.message)
    }

    @Test
    fun 파싱할때_키가_null이면_예외() {
        // given
        every { keyLocator.locate(any()) } returns null
        val idToken = Jwts.builder()
            .audience().add("client-id")
            .and()
            .issuer("https://appleid.apple.com")
            .signWith(key)
            .expiration(Date.from(clock.instant().plus(1, ChronoUnit.DAYS)))
            .compact()

        // when & then
        Assertions.assertThatThrownBy { appleOpenIdClient.getUserInfo(idToken) }
            .isInstanceOf(UnauthorizedException::class.java)
            .hasMessage(ErrorCode.OPEN_ID_INVALID_TOKEN.message)
    }

    @Test
    fun audience_issuer가_올바르면_성공() {
        // given
        every { keyLocator.locate(any()) } returns key
        val socialId = "12345"
        val idToken = Jwts.builder()
            .audience().add("appleClientId")
            .and()
            .issuer("https://appleid.apple.com")
            .signWith(key)
            .subject(socialId)
            .claim("nonce", "value")
            .expiration(Date.from(clock.instant().plus(1, ChronoUnit.DAYS)))
            .compact()

        // when
        val expect = appleOpenIdClient.getUserInfo(idToken)

        // then
        Assertions.assertThat(expect.socialId).isEqualTo(socialId)
    }

    @Test
    fun audience_값은_apple_client_id_와_같으면_성공() {
        // given
        every { keyLocator.locate(any()) } returns key
        val socialId = "12345"
        val idToken = Jwts.builder()
            .audience().add("appleClientId")
            .and()
            .issuer("https://appleid.apple.com")
            .signWith(key)
            .subject(socialId)
            .claim("nonce", "value")
            .expiration(Date.from(clock.instant().plus(1, ChronoUnit.DAYS)))
            .compact()

        // when
        val expect = appleOpenIdClient.getUserInfo(idToken)

        // then
        Assertions.assertThat(expect.socialId).isEqualTo(socialId)
    }
}
