package com.festago.auth.infrastructure.openid

import io.jsonwebtoken.security.JwkSet
import io.jsonwebtoken.security.Jwks
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier
import java.util.stream.IntStream
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CachedOpenIdKeyProviderTest {

    lateinit var cachedOpenIdKeyProvider: CachedOpenIdKeyProvider

    var jwksJson = """
        {
            "keys": [
                {
                    "kid": "1",
                    "kty": "RSA",
                    "alg": "RS256",
                    "use": "sig",
                    "n": "q8zZ0b_MNaLd6Ny8wd4cjFomilLfFIZcmhNSc1ttx_oQdJJZt5CDHB8WWwPGBUDUyY8AmfglS9Y1qA0_fxxs-ZUWdt45jSbUxghKNYgEwSutfM5sROh3srm5TiLW4YfOvKytGW1r9TQEdLe98ork8-rNRYPybRI3SKoqpci1m1QOcvUg4xEYRvbZIWku24DNMSeheytKUz6Ni4kKOVkzfGN11rUj1IrlRR-LNA9V9ZYmeoywy3k066rD5TaZHor5bM5gIzt1B4FmUuFITpXKGQZS5Hn_Ck8Bgc8kLWGAU8TzmOzLeROosqKE0eZJ4ESLMImTb2XSEZuN1wFyL0VtJw",
                    "e": "AQAB"
                }
            ]
        }
        """.trimIndent()

    @BeforeEach
    fun setUp() {
        cachedOpenIdKeyProvider = CachedOpenIdKeyProvider()
    }

    @Test
    fun kid에_대한_JWK_키가_있으면_null이_아니다() {
        // given
        val jwtSet = Jwks.setParser()
            .build()
            .parse(jwksJson)

        // when
        val actual = cachedOpenIdKeyProvider.provide("1") { jwtSet }

        // then
        actual shouldNotBe null
    }

    @Test
    fun kid에_대한_JWK_키가_없으면_null이다() {
        // given
        val jwtSet = Jwks.setParser()
            .build()
            .parse(jwksJson)

        // when
        val actual = cachedOpenIdKeyProvider.provide("2") { jwtSet }

        // then
        actual shouldBe null
    }

    @Test
    fun kid에_대한_JWK_키가_캐싱되어야_한다() {
        // given
        val jwtSet = Jwks.setParser()
            .build()
            .parse(jwksJson)
        val jwkSetSupplier: Supplier<JwkSet> = mockk()
        every { jwkSetSupplier.get() } returns jwtSet

        cachedOpenIdKeyProvider.provide("1", jwkSetSupplier)

        // when
        cachedOpenIdKeyProvider.provide("1", jwkSetSupplier)

        // then
        verify(exactly = 1) { jwkSetSupplier.get() }
    }

    @Test
    fun 동시에_요청이_와도_캐시에_값을_갱신하는_로직은_한_번만_호출된다() {
        // given
        val jwtSet = Jwks.setParser()
            .build()
            .parse(jwksJson)
        val jwkSetSupplier: Supplier<JwkSet> = mockk()
        every { jwkSetSupplier.get() } returns jwtSet

        // when
        val futures = IntStream.rangeClosed(1, 10)
            .mapToObj {
                CompletableFuture.runAsync {
                    cachedOpenIdKeyProvider.provide(
                        "1",
                        jwkSetSupplier
                    )
                }
            }
            .toList()
        futures.forEach { it.join() }

        // then
        verify(exactly = 1) { jwkSetSupplier.get() }
    }
}