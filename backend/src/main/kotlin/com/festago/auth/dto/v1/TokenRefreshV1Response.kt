package com.festago.auth.dto.v1

import com.festago.auth.dto.TokenResponse

data class TokenRefreshV1Response(
    val accessToken: TokenResponse,
    val refreshToken: TokenResponse,
)
