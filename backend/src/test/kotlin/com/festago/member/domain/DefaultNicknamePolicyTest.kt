package com.festago.member.domain

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class DefaultNicknamePolicyTest {

    @Test
    fun 형용사와_명사가_합쳐진_닉네임이_반환된다() {
        // given
        val defaultNicknamePolicy = DefaultNicknamePolicy(
            adjectives = listOf("춤추는"),
            nouns = listOf("다람쥐")
        )

        // when
        val nickname = defaultNicknamePolicy.generate()

        // then
        Assertions.assertThat(nickname).isEqualTo("춤추는 다람쥐")
    }
}
