package com.festago.auth.infrastructure.token.jwt.claims

import com.festago.auth.domain.Role
import io.jsonwebtoken.Jwts
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

internal class AdminAuthenticationClaimsExtractorTest {

    val adminAuthenticationClaimsExtractor = AdminAuthenticationClaimsExtractor()

    @ParameterizedTest
    @EnumSource(names = ["ADMIN"], mode = EnumSource.Mode.EXCLUDE)
    fun Claims의_audience가_ADMIN이_아니면_반환되는_Authentication의_권한은_ANONYMOUS이다(role: Role) {
        // given
        val claims = Jwts.claims()
            .audience().add(role.name).and()
            .build()

        // when
        val actual = adminAuthenticationClaimsExtractor.extract(claims)

        // then
        actual.role shouldBe Role.ANONYMOUS
    }

    @Test
    fun Claims의_audience가_ADMIN이면_Authentication의_권한은_ADMIN이다() {
        // given
        val claims = Jwts.claims()
            .audience().add(Role.ADMIN.name).and()
            .add("adminId", 1L)
            .build()

        // when
        val actual = adminAuthenticationClaimsExtractor.extract(claims)

        // then
        actual.role shouldBe Role.ADMIN
    }
}
