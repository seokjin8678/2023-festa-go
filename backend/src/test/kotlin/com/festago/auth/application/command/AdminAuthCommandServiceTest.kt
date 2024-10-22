package com.festago.auth.application.command

import com.festago.admin.domain.Admin
import com.festago.admin.repository.AdminRepository
import com.festago.admin.repository.MemoryAdminRepository
import com.festago.auth.domain.token.jwt.provider.AdminAuthenticationTokenProvider
import com.festago.auth.dto.TokenResponse
import com.festago.auth.dto.command.AdminLoginCommand
import com.festago.auth.dto.command.AdminSignupCommand
import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.ForbiddenException
import com.festago.common.exception.UnauthorizedException
import com.festago.support.fixture.AdminFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.factory.PasswordEncoderFactories

class AdminAuthCommandServiceTest {

    lateinit var adminRepository: AdminRepository

    lateinit var adminAuthenticationTokenProvider: AdminAuthenticationTokenProvider

    lateinit var adminAuthCommandService: AdminAuthCommandService

    @BeforeEach
    fun setUp() {
        adminRepository = MemoryAdminRepository()
        adminAuthenticationTokenProvider = mockk()
        adminAuthCommandService = AdminAuthCommandService(
            adminAuthenticationTokenProvider,
            adminRepository,
            PasswordEncoderFactories.createDelegatingPasswordEncoder()
        )
    }

    @Nested
    inner class 로그인 {

        @Test
        fun 계정이_없으면_예외() {
            // given
            val command = AdminLoginCommand("admin", "password")

            // when
            val ex = shouldThrow<UnauthorizedException> {
                adminAuthCommandService.login(command)
            }

            // then
            ex shouldNotBe ErrorCode.INCORRECT_PASSWORD_OR_ACCOUNT.message
        }

        @Test
        fun 비밀번호가_틀리면_예외() {
            // given
            adminRepository.save(
                AdminFixture.builder()
                    .username("admin")
                    .password("{noop}password")
                    .build()
            )
            val command = AdminLoginCommand("admin", "wrongPassword")

            // when
            val ex = shouldThrow<UnauthorizedException> {
                adminAuthCommandService.login(command)
            }

            // then
            ex shouldHaveMessage ErrorCode.INCORRECT_PASSWORD_OR_ACCOUNT.message
        }

        @Test
        fun 성공() {
            // given
            adminRepository.save(
                AdminFixture.builder()
                    .username("admin")
                    .password("{noop}password")
                    .build()
            )
            val command = AdminLoginCommand("admin", "password")
            every { adminAuthenticationTokenProvider.provide(any()) } returns TokenResponse(
                "token",
                LocalDateTime.now().plusWeeks(1)
            )

            // when
            val result = adminAuthCommandService.login(command)

            // then
            result.accessToken shouldBe "token"
        }
    }

    @Nested
    inner class 가입 {

        @Test
        fun 닉네임이_중복이면_예외() {
            // given
            val rootAdmin = adminRepository.save(Admin.createRootAdmin("{noop}password"))
            val command = AdminSignupCommand("admin", "password")

            // when
            val ex = shouldThrow<BadRequestException> {
                adminAuthCommandService.signup(rootAdmin.identifier, command)
            }

            // then
            ex shouldHaveMessage ErrorCode.DUPLICATE_ACCOUNT_USERNAME.message
        }

        @Test
        fun Root_어드민이_아니면_예외() {
            // given
            val admin = adminRepository.save(
                AdminFixture.builder()
                    .username("glen")
                    .password("{noop}password")
                    .build()
            )
            val command = AdminSignupCommand("newAdmin", "password")

            // when
            val ex = shouldThrow<ForbiddenException> {
                adminAuthCommandService.signup(admin.identifier, command)
            }

            // then
            ex shouldHaveMessage ErrorCode.NOT_ENOUGH_PERMISSION.message
        }

        @Test
        fun 성공() {
            // given
            val rootAdmin = adminRepository.save(Admin.createRootAdmin("{noop}password"))
            val command = AdminSignupCommand("newAdmin", "password")

            // when
            adminAuthCommandService.signup(rootAdmin.identifier, command)

            // then
            adminRepository.existsByUsername(command.username) shouldBe true
        }
    }

    @Nested
    inner class 루트_어드민_초기화 {

        @Test
        fun 루트_어드민을_활성화하면_저장된다() {
            // when
            adminAuthCommandService.initializeRootAdmin("1234")

            // then
            val rootAdmin = Admin.createRootAdmin("1234")
            adminRepository.existsByUsername(rootAdmin.username) shouldBe true
        }

        @Test
        fun 루트_어드민이_존재하는데_초기화하면_예외() {
            // given
            adminAuthCommandService.initializeRootAdmin("1234")

            // when & then
            val ex = shouldThrow<BadRequestException> {
                adminAuthCommandService.initializeRootAdmin("1234")
            }
            ex shouldHaveMessage ErrorCode.DUPLICATE_ACCOUNT_USERNAME.message
        }
    }
}
