package com.festago.auth.dto.v1

import com.festago.auth.dto.TokenResponse

data class LoginV1Response(
    val nickname: String,
    val profileImageUrl: String,
    val accessToken: TokenResponse,
    val refreshToken: TokenResponse,
)
