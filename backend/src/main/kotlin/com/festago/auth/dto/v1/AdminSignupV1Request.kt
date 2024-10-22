package com.festago.auth.dto.v1

import com.festago.auth.dto.command.AdminSignupCommand
import jakarta.validation.constraints.NotBlank

data class AdminSignupV1Request(
    @NotBlank
    val username: String,
    @NotBlank
    val password: String,
) {
    fun toCommand(): AdminSignupCommand {
        return AdminSignupCommand(
            username = username,
            password = password
        )
    }
}
