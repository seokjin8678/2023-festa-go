package com.festago.auth.dto.command

import java.time.LocalDateTime
import java.util.UUID

data class LoginResult(
    val memberId: Long,
    val nickname: String,
    val profileImageUrl: String,
    val refreshToken: UUID,
    val refreshTokenExpiredAt: LocalDateTime,
)
