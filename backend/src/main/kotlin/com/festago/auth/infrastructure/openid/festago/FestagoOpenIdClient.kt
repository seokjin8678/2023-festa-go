package com.festago.auth.infrastructure.openid.festago

import com.festago.auth.domain.SocialType
import com.festago.auth.domain.model.UserInfo
import com.festago.auth.domain.openid.OpenIdClient
import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!prod")
internal class FestagoOpenIdClient : OpenIdClient {
    private val userInfoMap: Map<String, UserInfo> = mapOf(
        "1" to UserInfo("1", SocialType.FESTAGO, "member1", PROFILE_IMAGE),
        "2" to UserInfo("2", SocialType.FESTAGO, "member2", PROFILE_IMAGE),
        "3" to UserInfo("3", SocialType.FESTAGO, "member3", PROFILE_IMAGE),
    )

    override fun getUserInfo(idToken: String): UserInfo {
        return userInfoMap[idToken] ?: throw BadRequestException(ErrorCode.OPEN_ID_INVALID_TOKEN)
    }

    final override val socialType = SocialType.FESTAGO

    companion object {
        private const val PROFILE_IMAGE = "https://placehold.co/150x150"
    }
}
