package com.festago.auth.application.command

import com.festago.auth.domain.RefreshTokenRepository
import com.festago.auth.domain.SocialType
import com.festago.auth.domain.UserInfoMemberMapper
import com.festago.auth.domain.model.UserInfo
import com.festago.auth.infrastructure.repository.MemoryRefreshTokenRepository
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.common.exception.UnauthorizedException
import com.festago.member.domain.DefaultNicknamePolicy
import com.festago.member.domain.MemberRepository
import com.festago.member.infrastructure.repository.MemoryMemberRepository
import com.festago.support.fixture.MemberFixture
import com.festago.support.fixture.RefreshTokenFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import java.time.Clock
import java.time.LocalDateTime
import java.util.UUID
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MemberAuthCommandServiceTest {

    lateinit var memberAuthCommandService: MemberAuthCommandService

    lateinit var memberRepository: MemberRepository

    lateinit var refreshTokenRepository: RefreshTokenRepository

    lateinit var clock: Clock

    @BeforeEach
    fun setUp() {
        clock = spyk(Clock.systemDefaultZone())
        memberRepository = MemoryMemberRepository()
        refreshTokenRepository = MemoryRefreshTokenRepository()
        val defaultNicknamePolicy = mockk<DefaultNicknamePolicy>().apply {
            every { generate() } returns "nickname"
        }
        memberAuthCommandService = MemberAuthCommandService(
            memberRepository = memberRepository,
            refreshTokenRepository = refreshTokenRepository,
            eventPublisher = mockk(relaxed = true),
            userInfoMemberMapper = UserInfoMemberMapper(defaultNicknamePolicy),
            clock = clock
        )
    }

    @Nested
    inner class login {
        @Test
        fun 신규_회원으로_로그인하면_회원과_리프래쉬_토큰이_저장된다() {
            // when
            val actual = memberAuthCommandService.login(getUserInfo("1"))

            // then
            memberRepository.findById(actual.memberId) shouldNotBe null
            refreshTokenRepository.findById(actual.refreshToken) shouldNotBe null
        }

        @Test
        fun 기존_회원으로_로그인해도_기존_리프레쉬_토큰이_삭제되지_않는다() {
            // given
            val member = memberRepository.save(MemberFixture.builder().build())
            val originToken = refreshTokenRepository.save(
                RefreshTokenFixture.builder().memberId(member.id).build()
            )

            // when
            val actual = memberAuthCommandService.login(getUserInfo(member.socialId!!))

            // then
            refreshTokenRepository.findById(originToken.id) shouldNotBe null
            refreshTokenRepository.findById(actual.refreshToken) shouldNotBe null
        }
    }

    @Nested
    inner class logout {
        @Test
        fun 회원의_리프래쉬_토큰이_삭제된다() {
            // given
            val member = memberRepository.save(MemberFixture.builder().build())
            val originToken = refreshTokenRepository.save(
                RefreshTokenFixture.builder().memberId(member.id).build()
            )

            // when
            memberAuthCommandService.logout(member.identifier, originToken.id)

            // then
            refreshTokenRepository.findById(originToken.id) shouldBe null
        }

        @Test
        fun 다른_회원의_리프래쉬_토큰으로_로그아웃하면_해당_리프래쉬_토큰은_삭제되지_않는다() {
            // given
            val 회원A = memberRepository.save(MemberFixture.builder().build())
            val 회원B = memberRepository.save(MemberFixture.builder().build())
            val 회원A_리프래쉬_토큰 = refreshTokenRepository.save(
                RefreshTokenFixture.builder().memberId(회원A.id).build()
            )

            // when
            memberAuthCommandService.logout(회원B.identifier, 회원A_리프래쉬_토큰.id)

            // then
            refreshTokenRepository.findById(회원A_리프래쉬_토큰.id) shouldNotBe null
        }
    }

    @Nested
    inner class refresh {
        @Test
        fun 기존_리프래쉬_토큰이_있으면_기존_리프래쉬_토큰을_삭제하고_새로운_토큰을_저장한다() {
            // given
            val member = memberRepository.save(MemberFixture.builder().build())
            val originToken = refreshTokenRepository.save(
                RefreshTokenFixture.builder().memberId(member.id).build()
            )

            // when
            val actual = memberAuthCommandService.refresh(originToken.id)

            // then
            refreshTokenRepository.findById(originToken.id) shouldBe null
            refreshTokenRepository.findById(UUID.fromString(actual.token)) shouldNotBe null
        }

        @Test
        fun 기존_리프래쉬_토큰이_없으면_예외가_발생한다() {
            // when
            val ex = shouldThrow<UnauthorizedException> {
                memberAuthCommandService.refresh(UUID.randomUUID())
            }

            // then
            ex shouldHaveMessage ErrorCode.INVALID_REFRESH_TOKEN.message
        }

        @Test
        fun 리프래쉬를_요청한_리프래쉬_토큰이_만료되면_예외가_발생한다() {
            // given
            val member = memberRepository.save(MemberFixture.builder().build())
            val yesterday = LocalDateTime.now().minusDays(1)
            val expiredToken = refreshTokenRepository.save(
                RefreshTokenFixture.builder().memberId(member.id).expiredAt(yesterday).build()
            )

            // when
            val ex = shouldThrow<UnauthorizedException> {
                memberAuthCommandService.refresh(expiredToken.id)
            }

            // then
            ex shouldHaveMessage ErrorCode.EXPIRED_REFRESH_TOKEN.message
        }
    }

    @Nested
    inner class deleteAccount {

        @Test
        fun 해당_회원이_없으면_예외() {
            // when
            val ex = shouldThrow<NotFoundException> {
                memberAuthCommandService.deleteAccount(4885L)
            }

            // then
            ex shouldHaveMessage ErrorCode.MEMBER_NOT_FOUND.message
        }

        @Test
        fun 해당_회원이_있으면_회원이_삭제된다() {
            // given
            val member = memberRepository.save(MemberFixture.builder().build())

            // when
            memberAuthCommandService.deleteAccount(member.identifier)

            // then
            memberRepository.findById(member.identifier) shouldBe null
        }
    }

    private fun getUserInfo(socialId: String): UserInfo {
        return UserInfo(
            socialId,
            SocialType.FESTAGO,
            "오리",
            "https://image.com/image.png"
        )
    }
}
