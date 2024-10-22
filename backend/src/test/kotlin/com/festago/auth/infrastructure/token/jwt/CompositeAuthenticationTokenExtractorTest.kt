package com.festago.auth.infrastructure.token.jwt

import com.festago.auth.domain.Role
import com.festago.auth.domain.authentication.AdminAuthentication
import com.festago.auth.domain.authentication.AnonymousAuthentication
import com.festago.auth.domain.token.jwt.JwtTokenParser
import com.festago.auth.domain.token.jwt.claims.AuthenticationClaimsExtractor
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CompositeAuthenticationTokenExtractorTest {

    lateinit var jwtTokenParser: JwtTokenParser

    @BeforeEach
    fun setUp() {
        jwtTokenParser = mockk()
        every { jwtTokenParser.getClaims(any()) } returns mockk()
    }

    @Test
    fun AuthenticationClaimsExtractors_모두_AnonymousAuthentication을_반환하면_권한이_Anonymous인_Authentication을_반환한다() {
        // given
        val compositeAuthenticationTokenExtractor = CompositeAuthenticationTokenExtractor(
            jwtTokenParser,
            listOf(
                AuthenticationClaimsExtractor { AnonymousAuthentication },
                AuthenticationClaimsExtractor { AnonymousAuthentication },
            )
        )

        // when
        val actual = compositeAuthenticationTokenExtractor.extract("token")

        // then
        actual shouldBe AnonymousAuthentication
    }

    @Test
    fun AuthenticationClaimsExtractors_중_하나라도_AnonymousAuthentication이_아닌_값을_반환하면_해당_값을_반환한다() {
        // given
        val compositeAuthenticationTokenExtractor = CompositeAuthenticationTokenExtractor(
            jwtTokenParser,
            listOf(
                AuthenticationClaimsExtractor { AnonymousAuthentication },
                AuthenticationClaimsExtractor { AdminAuthentication(4885) },
            )
        )

        // when
        val actual = compositeAuthenticationTokenExtractor.extract("token")

        // then
        actual.role shouldBe Role.ADMIN
        actual.id shouldBe 4885
    }
}
