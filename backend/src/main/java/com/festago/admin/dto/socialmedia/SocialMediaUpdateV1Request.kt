package com.festago.admin.dto.socialmedia

import com.festago.socialmedia.dto.command.SocialMediaUpdateCommand
import jakarta.validation.constraints.NotBlank

data class SocialMediaUpdateV1Request(
    @NotBlank val name: String,
    val logoUrl: String?,
    @NotBlank val url: String,
) {
    fun toCommand(): SocialMediaUpdateCommand {
        return SocialMediaUpdateCommand(
            name = name,
            logoUrl = logoUrl,
            url = url,
        )
    }
}
