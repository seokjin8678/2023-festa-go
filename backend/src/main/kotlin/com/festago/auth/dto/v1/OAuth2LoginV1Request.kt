package com.festago.auth.dto.v1

import com.festago.auth.domain.SocialType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class OAuth2LoginV1Request(
    @field:NotNull
    val socialType: SocialType,
    @field:NotBlank
    val code: String,
)
