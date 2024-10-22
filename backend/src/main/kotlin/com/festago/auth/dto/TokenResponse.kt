package com.festago.auth.dto

import java.time.LocalDateTime

data class TokenResponse(
    val token: String,
    val expiredAt: LocalDateTime,
)
