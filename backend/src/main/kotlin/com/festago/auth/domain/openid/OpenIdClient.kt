package com.festago.auth.domain.openid

import com.festago.auth.domain.SocialType
import com.festago.auth.domain.model.UserInfo

interface OpenIdClient {
    fun getUserInfo(idToken: String): UserInfo

    val socialType: SocialType
}
