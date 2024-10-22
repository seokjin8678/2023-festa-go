package com.festago.auth.dto.v1

import com.festago.auth.dto.command.AdminLoginCommand
import jakarta.validation.constraints.NotBlank

data class AdminLoginV1Request(
    @NotBlank
    val username: String,
    @NotBlank
    val password: String,
) {
    fun toCommand(): AdminLoginCommand {
        return AdminLoginCommand(
            username = username,
            password = password
        )
    }
}
