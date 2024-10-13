package com.festago.auth.infrastructure.oauth2.kakao

import com.festago.auth.domain.SocialType
import com.festago.auth.domain.model.UserInfo
import com.festago.auth.domain.oauth2.OAuth2Client
import com.festago.auth.infrastructure.openid.kakao.KakaoOpenIdClient
import org.springframework.stereotype.Component

@Component
internal class KakaoOAuth2Client(
    private val kakaoOAuth2TokenClient: KakaoOAuth2TokenClient,
    private val kakaoOpenIdClient: KakaoOpenIdClient,
) : OAuth2Client {

    override fun getUserInfo(code: String): UserInfo {
        val idToken = kakaoOAuth2TokenClient.getIdToken(code)
        return kakaoOpenIdClient.getUserInfo(idToken)
    }

    final override val socialType: SocialType = SocialType.KAKAO
}
