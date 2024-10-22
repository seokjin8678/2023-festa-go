package com.festago.auth.dto.v1

import jakarta.validation.constraints.NotBlank

data class RootAdminInitializeRequest(
    @field:NotBlank
    val password: String,
)
