package com.festago.auth.domain

import io.kotest.matchers.shouldBe
import java.time.LocalDateTime
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class RefreshTokenTest {

    @Nested
    inner class isExpired {

        @Test
        fun 주어진_시간이_만료시간보다_이후이면_참() {
            // given
            val now = LocalDateTime.parse("2077-06-30T18:00:00")
            val future = now.plusSeconds(1)
            val refreshToken = RefreshToken(1L, now)

            // when
            val actual = refreshToken.isExpired(future)

            // then
            actual shouldBe true
        }

        @ParameterizedTest
        @ValueSource(longs = [0, 1])
        fun 주어진_시간이_만료시간보다_같거나_이전이면_거짓(second: Long) {
            // given
            val now = LocalDateTime.parse("2077-06-30T18:00:00")
            val past = now.minusSeconds(second)
            val refreshToken = RefreshToken(1L, now)

            // when
            val actual = refreshToken.isExpired(past)

            // then
            actual shouldBe false
        }
    }

    @Nested
    inner class isOwner {
        @Test
        fun 주어진_식별자가_자신의_memberId와_같으면_참() {
            // given
            val memberId = 1L
            val refreshToken = RefreshToken(memberId, LocalDateTime.now())

            // when
            val actual = refreshToken.isOwner(memberId)

            // then
            actual shouldBe true
        }

        @Test
        fun 주어진_식별자가_자신의_memberId와_다르면_거짓() {
            // given
            val memberId = 1L
            val otherId = 2L
            val refreshToken = RefreshToken(memberId, LocalDateTime.now())

            // when
            val actual = refreshToken.isOwner(otherId)

            // then
            actual shouldBe false
        }
    }
}
