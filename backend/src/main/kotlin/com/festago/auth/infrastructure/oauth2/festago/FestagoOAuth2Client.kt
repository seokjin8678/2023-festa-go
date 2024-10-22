package com.festago.auth.infrastructure.oauth2.festago

import com.festago.auth.domain.SocialType
import com.festago.auth.domain.model.UserInfo
import com.festago.auth.domain.oauth2.OAuth2Client
import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!prod")
internal class FestagoOAuth2Client : OAuth2Client {

    private val userInfoMap: Map<String, UserInfo> = mapOf(
        "1" to UserInfo("1", socialType, "member1", ""),
        "2" to UserInfo("2", socialType, "member1", ""),
        "3" to UserInfo("3", socialType, "member1", ""),
    )

    override fun getUserInfo(code: String): UserInfo {
        val userInfo = userInfoMap[code]
            ?: throw BadRequestException(ErrorCode.OAUTH2_INVALID_TOKEN)
        return userInfo
    }

    final override val socialType: SocialType
        get() = SocialType.FESTAGO
}
