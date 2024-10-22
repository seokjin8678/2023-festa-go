package com.festago.admin.dto.artist

import com.festago.artist.dto.command.ArtistUpdateCommand
import jakarta.validation.constraints.NotBlank

data class ArtistV1UpdateRequest(
    @field:NotBlank
    val name: String,
    val profileImageUrl: String?,
    val backgroundImageUrl: String?,
) {

    fun toCommand(): ArtistUpdateCommand {
        return ArtistUpdateCommand(
            name = name,
            profileImageUrl = profileImageUrl,
            backgroundImageUrl = backgroundImageUrl
        )
    }
}
