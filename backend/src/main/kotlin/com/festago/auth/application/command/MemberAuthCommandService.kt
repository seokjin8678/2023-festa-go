package com.festago.auth.application.command

import com.festago.auth.domain.RefreshToken
import com.festago.auth.domain.RefreshTokenRepository
import com.festago.auth.domain.UserInfoMemberMapper
import com.festago.auth.domain.model.UserInfo
import com.festago.auth.dto.command.LoginResult
import com.festago.auth.dto.command.TokenRefreshResult
import com.festago.auth.dto.event.MemberDeletedEvent
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.UnauthorizedException
import com.festago.member.domain.Member
import com.festago.member.domain.MemberRepository
import com.festago.member.domain.getOrThrow
import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.Clock
import java.time.LocalDateTime
import java.util.UUID
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
@Transactional
class MemberAuthCommandService(
    private val memberRepository: MemberRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val eventPublisher: ApplicationEventPublisher,
    private val userInfoMemberMapper: UserInfoMemberMapper,
    private val clock: Clock,
) {

    fun login(userInfo: UserInfo): LoginResult {
        val member = memberRepository.findBySocialIdAndSocialType(userInfo.socialId, userInfo.socialType)
            ?: signUp(userInfo)
        val memberId = member.identifier
        val refreshToken = createSavedRefreshToken(memberId)
        return LoginResult(
            memberId = memberId,
            nickname = member.nickname,
            profileImageUrl = member.profileImage,
            refreshToken = refreshToken.id,
            refreshTokenExpiredAt = refreshToken.expiredAt
        )
    }

    private fun signUp(userInfo: UserInfo): Member {
        val member = userInfoMemberMapper.toMember(userInfo)
        return memberRepository.save(member)
    }

    private fun createSavedRefreshToken(memberId: Long): RefreshToken {
        return refreshTokenRepository.save(RefreshToken.of(memberId, LocalDateTime.now(clock)))
    }

    fun logout(memberId: Long, refreshTokenId: UUID) {
        val refreshToken = refreshTokenRepository.findById(refreshTokenId) ?: return
        if (refreshToken.isOwner(memberId)) {
            refreshTokenRepository.deleteById(refreshTokenId)
        }
    }

    fun refresh(refreshTokenId: UUID): TokenRefreshResult {
        val refreshToken: RefreshToken = refreshTokenRepository.findById(refreshTokenId)
            ?: run {
                log.warn { "탈취 가능성이 있는 리프래쉬 토큰이 있습니다. token=${refreshTokenId}" }
                throw UnauthorizedException(ErrorCode.INVALID_REFRESH_TOKEN)
            }
        if (refreshToken.isExpired(LocalDateTime.now(clock))) {
            log.info { "만료된 리프래쉬 토큰이 있습니다. memberId=${refreshToken.memberId}, token=${refreshTokenId}" }
            throw UnauthorizedException(ErrorCode.EXPIRED_REFRESH_TOKEN)
        }
        refreshTokenRepository.deleteById(refreshTokenId)
        val newRefreshToken = createSavedRefreshToken(refreshToken.memberId)
        return TokenRefreshResult(
            memberId = newRefreshToken.memberId,
            token = newRefreshToken.id.toString(),
            expiredAt = newRefreshToken.expiredAt
        )
    }

    fun deleteAccount(memberId: Long) {
        val member = memberRepository.getOrThrow(memberId)
        log.info { "Member Deleted. memberId: ${member.id}, socialType: ${member.socialType}, socialId: ${member.socialId}" }
        memberRepository.delete(member)
        eventPublisher.publishEvent(MemberDeletedEvent(member))
    }
}
