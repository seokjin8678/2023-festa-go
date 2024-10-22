package com.festago.auth.application.command

import com.festago.auth.domain.SocialType
import com.festago.auth.domain.authentication.MemberAuthentication
import com.festago.auth.domain.model.UserInfo
import com.festago.auth.domain.oauth2.OAuth2Clients
import com.festago.auth.domain.openid.OpenIdClients
import com.festago.auth.domain.token.jwt.provider.MemberAuthenticationTokenProvider
import com.festago.auth.dto.TokenResponse
import com.festago.auth.dto.v1.LoginV1Response
import com.festago.auth.dto.v1.TokenRefreshV1Response
import java.util.UUID
import org.springframework.stereotype.Service

@Service
class MemberAuthFacadeService(
    private val oAuth2Clients: OAuth2Clients,
    private val openIdClients: OpenIdClients,
    private val memberAuthCommandService: MemberAuthCommandService,
    private val authTokenProvider: MemberAuthenticationTokenProvider,
) {

    fun oAuth2Login(socialType: SocialType, code: String): LoginV1Response {
        val oAuth2Client = oAuth2Clients.getClient(socialType)
        val userInfo = oAuth2Client.getUserInfo(code)
        return login(userInfo)
    }

    private fun login(userInfo: UserInfo): LoginV1Response {
        val loginResult = memberAuthCommandService.login(userInfo)
        val accessToken = authTokenProvider.provide(MemberAuthentication(loginResult.memberId))
        val refreshToken = TokenResponse(loginResult.refreshToken.toString(), loginResult.refreshTokenExpiredAt)
        return LoginV1Response(
            nickname = loginResult.nickname,
            profileImageUrl = loginResult.profileImageUrl,
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    fun openIdLogin(socialType: SocialType, idToken: String): LoginV1Response {
        val openIdClient = openIdClients.getClient(socialType)
        val userInfo = openIdClient.getUserInfo(idToken)
        return login(userInfo)
    }

    fun logout(memberId: Long, refreshTokenId: UUID) {
        memberAuthCommandService.logout(memberId, refreshTokenId)
    }

    fun refresh(refreshTokenId: UUID): TokenRefreshV1Response {
        val tokenRefreshResult = memberAuthCommandService.refresh(refreshTokenId)
        val memberId = tokenRefreshResult.memberId
        val accessToken = authTokenProvider.provide(MemberAuthentication(memberId))
        return TokenRefreshV1Response(
            accessToken = accessToken,
            refreshToken = TokenResponse(tokenRefreshResult.token, tokenRefreshResult.expiredAt)
        )
    }

    fun deleteAccount(memberId: Long) {
        memberAuthCommandService.deleteAccount(memberId)
    }
}
