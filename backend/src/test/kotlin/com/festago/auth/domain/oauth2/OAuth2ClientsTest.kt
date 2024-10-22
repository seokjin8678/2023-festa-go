package com.festago.auth.domain.oauth2

import com.festago.auth.domain.SocialType
import com.festago.auth.domain.oauth2.OAuth2Clients.Companion.builder
import com.festago.auth.infrastructure.oauth2.festago.FestagoOAuth2Client
import com.festago.auth.infrastructure.oauth2.kakao.KakaoOAuth2Client
import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.UnexpectedException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.mockk.mockk
import org.junit.jupiter.api.Test

internal class OAuth2ClientsTest {

    @Test
    fun 중복된_타입의_클라이언트가_주어지면_예외() {
        // given
        val builder = builder()
            .add(FestagoOAuth2Client())

        // when
        val ex = shouldThrow<UnexpectedException> {
            builder.add(FestagoOAuth2Client())
        }

        // then
        ex shouldHaveMessage "중복된 OAuth2 제공자 입니다."
    }

    @Test
    fun 해당_타입의_클라이언트가_없으면_예외() {
        // given
        val oAuth2Clients = builder()
            .build()

        // when
        val ex = shouldThrow<BadRequestException> {
            oAuth2Clients.getClient(SocialType.FESTAGO)
        }

        // then
        ex shouldHaveMessage ErrorCode.OAUTH2_NOT_SUPPORTED_SOCIAL_TYPE.message
    }

    @Test
    fun 여러_타입의_클라이언트가_주어졌을때_타입으로_찾기_성공() {
        // given
        val festagoOAuth2Client = FestagoOAuth2Client()
        val kakaoOAuth2Client = KakaoOAuth2Client(mockk(), mockk())

        val oAuth2Clients = builder()
            .add(festagoOAuth2Client)
            .add(kakaoOAuth2Client)
            .build()

        // when
        val expect = oAuth2Clients.getClient(SocialType.KAKAO)

        // then
        expect shouldBeSameInstanceAs kakaoOAuth2Client
    }
}
