package com.festago.auth.dto.v1

import com.festago.auth.domain.AuthType

data class AdminLoginV1Response(
    val username: String,
    val authType: AuthType,
)
