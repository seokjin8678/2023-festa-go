package com.festago.auth.dto.command

import com.festago.auth.domain.AuthType

data class AdminLoginResult(
    val username: String,
    val authType: AuthType,
    val accessToken: String,
)
