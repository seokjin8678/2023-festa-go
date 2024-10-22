package com.festago.auth.dto.command

data class AdminLoginCommand(
    val username: String,
    val password: String,
)
