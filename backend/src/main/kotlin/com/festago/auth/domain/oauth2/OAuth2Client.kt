package com.festago.auth.domain.oauth2

import com.festago.auth.domain.SocialType
import com.festago.auth.domain.model.UserInfo

interface OAuth2Client {
    fun getUserInfo(code: String): UserInfo

    val socialType: SocialType
}
