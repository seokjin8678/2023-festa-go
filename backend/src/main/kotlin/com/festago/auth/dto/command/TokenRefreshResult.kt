package com.festago.auth.dto.command

import java.time.LocalDateTime

data class TokenRefreshResult(
    val memberId: Long,
    val token: String,
    val expiredAt: LocalDateTime,
)
