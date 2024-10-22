package com.festago.auth.dto.v1

import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.UUID

data class RefreshTokenV1Request(
    @field:NotNull
    @field:UUID
    val refreshToken: String,
)
