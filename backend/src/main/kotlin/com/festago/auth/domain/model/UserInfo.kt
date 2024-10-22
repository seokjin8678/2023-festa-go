package com.festago.auth.domain.model

import com.festago.auth.domain.SocialType

data class UserInfo(
    val socialId: String,
    val socialType: SocialType,
    val nickname: String? = null,
    val profileImage: String? = null,
)
