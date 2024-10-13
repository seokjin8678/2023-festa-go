package com.festago.auth.infrastructure.token.jwt.claims

import com.festago.auth.domain.Role
import io.jsonwebtoken.Jwts
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

internal class MemberAuthenticationClaimsExtractorTest {

    val memberAuthenticationClaimsExtractor = MemberAuthenticationClaimsExtractor()

    @ParameterizedTest
    @EnumSource(value = Role::class, names = ["MEMBER"], mode = EnumSource.Mode.EXCLUDE)
    fun Claims의_audience가_MEMBER가_아니면_반환되는_Authentication의_권한은_ANONYMOUS이다(role: Role) {
        // given
        val claims = Jwts.claims()
            .audience().add(role.name).and()
            .build()

        // when
        val actual = memberAuthenticationClaimsExtractor.extract(claims)

        // then
        actual.role shouldBe Role.ANONYMOUS
    }

    @Test
    fun Claims의_audience가_MEMBER이면_Authentication의_권한은_MEMBER이다() {
        // given
        val claims = Jwts.claims()
            .audience().add(Role.MEMBER.name).and()
            .add("memberId", 1L)
            .build()

        // when
        val actual = memberAuthenticationClaimsExtractor.extract(claims)

        // then
        actual.role shouldBe Role.MEMBER
    }
}
