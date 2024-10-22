package com.festago.admin.domain

import com.festago.common.exception.ValidException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class AdminTest {

    @Test
    fun 어드민_생성_성공() {
        // given
        val admin = Admin("admin", "password")

        // when & then
        admin.username shouldBe "admin"
        admin.password shouldBe "password"
    }

    @Test
    fun username이_4글자_미만이면_예외() {
        // given
        val username = "1".repeat(3)

        // when & then
        shouldThrow<ValidException> { Admin(username, "password") }
    }

    @Test
    fun username이_20글자_초과하면_예외() {
        // given
        val username = "1".repeat(21)

        // when & then
        shouldThrow<ValidException> { Admin(username, "password") }
    }

    @Test
    fun password가_4글자_미만이면_예외() {
        // given
        val password = "1".repeat(3)

        // when & then
        shouldThrow<ValidException> { Admin("admin", password) }
    }

    @Test
    fun password가_255글자_초과하면_예외() {
        // given
        val password = "1".repeat(256)

        // when & then
        shouldThrow<ValidException> { Admin("admin", password) }
    }
}
