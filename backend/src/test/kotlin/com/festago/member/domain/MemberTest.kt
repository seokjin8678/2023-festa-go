package com.festago.member.domain

import com.festago.auth.domain.SocialType
import com.festago.common.exception.ValidException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EmptySource

class MemberTest {

    @Test
    fun Member_생성_성공() {
        // given
        val member = Member(1L, "12345", SocialType.FESTAGO, "nickname", "profileImage.png")

        // when & then
        member.id shouldBe 1L
    }

    @ParameterizedTest
    @EmptySource
    fun socialId가_공백이면_예외(socialId: String) {
        // when & then
        shouldThrow<ValidException> {
            Member(
                id = 1L,
                socialId = socialId,
                socialType = SocialType.FESTAGO,
                nickname = "nickname",
                profileImage = "profileImage.png"
            )
        }
    }

    @ParameterizedTest
    @EmptySource
    fun nickname이_공백이면_예외(nickname: String) {
        // when & then
        shouldThrow<ValidException> {
            Member(
                id = 1L,
                socialId = "12345",
                socialType = SocialType.FESTAGO,
                nickname = nickname,
                profileImage = "profileImage.png"
            )
        }
    }

    @Test
    fun nickname의_길이가_30자를_초과하면_예외() {
        // given
        val nickname = "1".repeat(31)

        // when & then
        shouldThrow<ValidException> {
            Member(
                id = 1L,
                socialId = "12345",
                socialType = SocialType.FESTAGO,
                nickname = nickname,
                profileImage = "profileImage.png"
            )
        }
    }

    @Test
    fun profileImage의_길이가_255자를_초과하면_예외() {
        // given
        val profileImage = "1".repeat(256)

        // when & then
        shouldThrow<ValidException> {
            Member(
                id = 1L,
                socialId = "12345",
                socialType = SocialType.FESTAGO,
                nickname = "nickname",
                profileImage = profileImage
            )
        }
    }
}
